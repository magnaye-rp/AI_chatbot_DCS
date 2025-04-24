from flask import Flask, request, jsonify
from flask_restful import Api, Resource, reqparse
import datetime
from datetime import date, timedelta
from dateutil import parser as date_parser
from functools import wraps
import pytz  # For timezone handling
from database_connector import execute_booking

app = Flask(__name__)
api = Api(app)

# Configuration
TIME_FORMAT = '%I:%M %p'
DATE_FORMATS = ['%Y-%m-%d', '%B %d', '%b %d', '%A', 'tomorrow']
WORKING_HOURS = (7, 17)
APPOINTMENT_DURATION = 30

# Store API keys (In production, use a proper database and hashing)
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

        # Check if within working hours
        if not (WORKING_HOURS[0] <= time_obj.hour < WORKING_HOURS[1]):
            return None, "Outside working hours (7 AM - 5 PM)"

        return time_obj, None
    except ValueError:
        return None, f"Invalid time format. Please use '{TIME_FORMAT}' (e.g., '02:30 PM')"


def validate_date(date_str):
    try:
        if date_str.lower() == "tomorrow":
            return date.today() + timedelta(days=1), None

        # Try parsing with dateutil which handles many formats
        date_obj = date_parser.parse(date_str).date()

        # Check if date is in the past
        if date_obj < date.today():
            return None, "Date cannot be in the past"

        # Check if weekday (Mon-Fri)
        if date_obj.weekday() >= 6:  # 5=Saturday, 6=Sunday
            return None, "We're closed on Sundays"

        return date_obj, None
    except ValueError:
        return None, f"Invalid date format. Accepted formats: {', '.join(DATE_FORMATS)}"


class BookAppointment(Resource):
    @authenticate_request
    def post(self):
        # Request argument parsing
        parser = reqparse.RequestParser()
        parser.add_argument('intent', type=str, required=True, help="Intent must be specified")
        parser.add_argument('date', type=str, required=True, help="Date is required")
        parser.add_argument('time', type=str, required=True, help="Time is required")
        parser.add_argument('service', type=str, required=True, help="Service type is required")
        parser.add_argument('patient_id', type=int, required=True, help="Patient ID is required")  # <-- Add patient_id
        args = parser.parse_args()

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
        end_time = (datetime.datetime.combine(date_obj, time_obj) +
                    timedelta(minutes=VALID_SERVICES[service])).time()
        if end_time.hour >= WORKING_HOURS[1]:
            return {
                "message": f"This service requires {VALID_SERVICES[service]} minutes. "
                           f"Please choose an earlier time slot."
            }, 400

        # Here we need to use patient_id instead of user_id
        patient_id = args['patient_id']  # <-- Use patient_id from the payload

        # Call the database connector to book the appointment using patient_id
        result = execute_booking(patient_id, date_obj.isoformat(), time_obj.strftime(TIME_FORMAT), service)

        if result['status'] == 'error':
            return {"message": result['message']}, 500
        else:
            appointment_details = {
                "patient_id": patient_id,  # <-- Make sure to return patient_id in the response
                "date": date_obj.isoformat(),
                "time": time_obj.strftime(TIME_FORMAT),
                "service": service,
                "status": "booked"
            }
            return {
                "message": "Appointment booked successfully",
                "appointment": appointment_details
            }, 200


# Add the BookAppointment resource to the API
api.add_resource(BookAppointment, "/book_appointment")

if __name__ == '__main__':
    app.run(debug=True, port=5000)
