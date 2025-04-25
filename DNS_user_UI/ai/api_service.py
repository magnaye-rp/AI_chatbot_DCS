from flask import Flask, request, jsonify
from flask_restful import Api, Resource, reqparse
import datetime
from datetime import date, timedelta
from dateutil import parser as date_parser
from functools import wraps
import logging
logging.basicConfig(level=logging.DEBUG)
from database_connector import execute_booking # Ensure this is correctly implemented

app = Flask(__name__)
api = Api(app)

TIME_FORMAT = '%I:%M %p'
MYSQL_TIME_FORMAT = '%H:%M:%S'
DATE_FORMATS = ['%Y-%m-%d', '%B %d', '%b %d', '%A', 'tomorrow']
WORKING_HOURS = (7, 17)
APPOINTMENT_DURATION = 30

API_KEYS = {
    "chatbot123": {"user_id": 123, "name": "Chatbot User"},
    "testkey456": {"user_id": 456, "name": "Test User"}
}

VALID_SERVICES = {
    "Consultation": 30,
    "Tooth Extraction": 60,
    "Dental Cleaning": 45,
    "Tooth Filling": 45,
    "Root Canal Treatment": 90,
    "Braces Adjustment": 30,
    "Teeth Whitening": 60,
    "Dental X-Ray": 15
}

def authenticate_request(func):
    """Decorator to authenticate API requests using an API key."""
    @wraps(func)
    def wrapper(*args, **kwargs):
        api_key = request.headers.get('X-API-Key')
        if not api_key or api_key not in API_KEYS:
            return {"message": "Invalid or missing API key"}, 403
        request.user_data = API_KEYS[api_key]
        return func(*args, **kwargs)

    return wrapper

def validate_time(time_str):
    try:
        time_obj = datetime.datetime.strptime(time_str, TIME_FORMAT).time()

        if not (WORKING_HOURS[0] <= time_obj.hour < WORKING_HOURS[1]):
            return None, "Outside working hours (7 AM - 5 PM)"

        return time_obj, None
    except ValueError:
        return None, f"Invalid time format. Please use '{TIME_FORMAT}' (e.g., '02:30 PM')"


def validate_date(date_str):
    try:
        if date_str.lower() == "tomorrow":
            return date.today() + timedelta(days=1), None

        date_obj = date_parser.parse(date_str).date()

        if date_obj < date.today():
            return None, "Date cannot be in the past"

        if date_obj.weekday() >= 6:
            return None, "We're closed on Sundays"

        return date_obj, None
    except ValueError:
        return None, f"Invalid date format. Accepted formats: {', '.join(DATE_FORMATS)}"


class BookAppointment(Resource):
    @authenticate_request
    def post(self):
        parser = reqparse.RequestParser()
        parser.add_argument('intent', type=str, required=True, help="Intent must be specified")
        parser.add_argument('date', type=str, required=True, help="Date is required")
        parser.add_argument('time', type=str, required=True, help="Time is required")
        parser.add_argument('service', type=str, required=True, help="Service type is required")
        parser.add_argument('patient_id', type=int, required=True, help="Patient ID is required")
        args = parser.parse_args()

        # Log the parsed arguments
        logging.debug(f"BookAppointment.post: Parsed arguments: {args}")

        # Validate service
        service = args['service'].title()
        if service not in VALID_SERVICES:
            return {
                "message": f"Invalid service type. Available services: {', '.join(VALID_SERVICES.keys())}",
                "valid_services": list(VALID_SERVICES.keys())
            }, 400

        # Validate date and time
        date_obj, date_error = validate_date(args['date'])
        if date_error:
            return {"message": date_error}, 400

        time_obj, time_error = validate_time(args['time'])
        if time_error:
            return {"message": time_error}, 400

        # Check appointment duration fits before closing
        duration = VALID_SERVICES[service]  # Get duration from VALID_SERVICES
        end_time = datetime.datetime.combine(date_obj, time_obj) + timedelta(minutes=duration)
        if end_time.time().hour >= WORKING_HOURS[1]:
            closing_time = f"{WORKING_HOURS[1]}:00 PM" if WORKING_HOURS[1] >= 12 else f"{WORKING_HOURS[1]}:00 AM"
            latest_start = (datetime.datetime.combine(date_obj, datetime.time(WORKING_HOURS[1], 0)) - timedelta(minutes=duration)).time()
            return {
                "message": (
                    f"This {service} requires {duration} minutes. Our clinic closes at {closing_time}. "
                    f"Please choose a time before {latest_start.strftime('%I:%M %p')} for this service."
                ),
                "max_start_time": latest_start.strftime('%I:%M %p'),
                "service_duration": duration,
                "closing_time": closing_time
            }, 400

        # Format the time for MySQL
        mysql_time_str = time_obj.strftime(MYSQL_TIME_FORMAT)
        logging.debug(f"BookAppointment.post: Calling execute_booking with pt_id={args['patient_id']}, date_={date_obj.isoformat()}, time_={mysql_time_str}, service={service}")

        # Execute booking
        try:
            logging.debug(f"BookAppointment.post: Calling execute_booking with patient_id={args['patient_id']}, date={date_obj.isoformat()}, time={mysql_time_str}, service={service}")
            result = execute_booking(
                pt_id=args['patient_id'],
                date_=date_obj.isoformat(),
                time_=mysql_time_str,
                service=service
            )
            logging.debug(f"BookAppointment.post: execute_booking result: {result}")

            if result.get('status') == 'error':
                return {"message": result['message']}, 400

            # Return consistent response format
            return {
                "message": "Appointment booked successfully",
                "appointment": {
                    "patient_id": args['patient_id'],
                    "date": date_obj.isoformat(),
                    "time": time_obj.strftime(TIME_FORMAT),
                    "service": service,
                    "duration": duration,
                    "status": "booked",
                    "confirmation_id": result.get('confirmation_id', 'N/A')
                }
            }, 200

        except Exception as e:
            logging.error(f"BookAppointment.post: Exception during booking: {e}")
            return {"message": f"Booking failed: {str(e)}"}, 500

api.add_resource(BookAppointment, "/book_appointment")

if __name__ == '__main__':
    app.run(debug=True, port=5000)