import mysql.connector
import json

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

def execute_booking(user_id, date, time, service=None):
    conn = get_db_connection()
    if conn is None:
        return {'status': 'error', 'message': 'Failed to connect to the database'}

    cursor = conn.cursor()
    try:
        sql = "CALL python_book_appointment(%s, %s, %s, %s)"
        values = (user_id, date, time, service)
        cursor.execute(sql, values)

        # ✅ Fetch the message returned from the stored procedure
        result = cursor.fetchall()
        message = result[0][0] if result else 'No response from procedure'

        conn.commit()
        cursor.close()
        conn.close()

        if "successfully" in message.lower():
            return {'status': 'success', 'message': message}
        else:
            return {'status': 'error', 'message': message}

    except mysql.connector.Error as err:
        conn.rollback()
        cursor.close()
        conn.close()
        return {'status': 'error', 'message': f"Error executing stored procedure: {err}"}
    except Exception as e:
        conn.rollback()
        cursor.close()
        conn.close()
        return {'status': 'error', 'message': str(e)}

if __name__ == '__main__':
    result = execute_booking(1, '2025-04-25', '14:00', 'general')
    print(result)
