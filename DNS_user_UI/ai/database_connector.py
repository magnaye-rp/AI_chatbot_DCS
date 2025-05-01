import mysql.connector
import json
DB_CONNECTOR_VERSION = "1.0"
# Load database configuration from a separate file (recommended for security)
with open('db_config.json', 'r') as f:
    db_config = json.load(f)

def get_db_connection():

    try:
        mydb = mysql.connector.connect(
            host=db_config['host'],
            user=db_config['user'],
            password=db_config['password'],
            database=db_config['database'],
            port=db_config['port']
        )
        return mydb
    except mysql.connector.Error as err:
        print(f"Error connecting to MySQL: {err}")
        return None

def execute_booking(pt_id, date_, time_, service=None):
    print(f"DEBUG: execute_booking called with {pt_id}, {date_}, {time_}, {service}")

    conn = get_db_connection()
    if conn is None:
        print("DEBUG: Database connection failed!")
        return {'status': 'error', 'message': 'Failed to connect to database'}

    print(f"DEBUG: Connected to database: {conn.server_host}:{conn.server_port}")
    conn = get_db_connection()
    if conn is None:
        return {'status': 'error', 'message': 'Failed to connect to the database'}

    cursor = conn.cursor()
    try:
        cursor.callproc('python_book_appointment', [pt_id, date_, time_, service])

        results = []
        for result_cursor in cursor.stored_results():  # `stored_results` is a property, no parentheses needed
            results = result_cursor.fetchall()  # Fetch results from the stored procedure

        conn.commit()

        return {
            "status": "success",
            "message": "Appointment booked successfully",
            "results": results
        }

    except mysql.connector.Error as err:
        # Handle MySQL errors
        conn.rollback()
        return {'status': 'error', 'message': f"Error executing stored procedure: {err}"}

    except Exception as e:
        # Handle general exceptions
        conn.rollback()
        return {'status': 'error', 'message': str(e)}

    finally:
        cursor.close()
        conn.close()

if __name__ == '__main__':
    result = execute_booking(1, '2025-04-25', '14:00', 'Tooth Extraction')
    print(result)

