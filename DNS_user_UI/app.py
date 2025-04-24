import customtkinter as ctk
from customtkinter import CTkLabel, CTkButton, CTkFrame, CTkScrollableFrame
from tkcalendar import DateEntry
from ai import chatbot
import mysql.connector
from datetime import datetime, timedelta
from collections import defaultdict
import sys
import mysql.connector
import tkinter as tk
from tkinter import ttk

print("PYTHON EXECUTABLE:", sys.executable)

#login page
def python_verify_login(number, password):
    try:
        conn = mysql.connector.connect(
            host="localhost",
            port=3308,
            user="root",
            password="",
            database="DentalClinicSystem"
        )
        cursor = conn.cursor()

        cursor.callproc('python_verify_login', [number, password])
        print("sumakses1")
        for result in cursor.stored_results():
            row = result.fetchone()
            print("sumakses2")
            if row:
                print("sumakses3")
                user_id, status = row
                return (status,user_id)
            else:
                print("Step by the Step")
                return {"status": "invalid", "id": None}

    except mysql.connector.Error as err:
        print("Database error:", err)
        return {"status": "error", "id": None}
    finally:
        if cursor:
            cursor.close()
        if conn:
            conn.close()
class LoginPage(ctk.CTk):
    def __init__(self):
        super().__init__()
        self.title("Login - Dental Care App")
        self.geometry("400x400")

        self.label = CTkLabel(self, text="Login", font=("Arial", 30, "bold"))
        self.label.pack(pady=30)

        self.phone_entry = ctk.CTkEntry(self, placeholder_text="Phone Number")
        self.phone_entry.pack(pady=10, padx=40)
        self.password_entry = ctk.CTkEntry(self, placeholder_text="Password", show="*")
        self.password_entry.pack(pady=10, padx=40)

        self.login_button = CTkButton(self, text="Login", command=self.try_login)
        self.login_button.pack(pady=20)

        self.status_label = CTkLabel(self, text="", text_color="red")
        self.status_label.pack()

    def try_login(self):
        number = self.phone_entry.get()
        password = self.password_entry.get()
        user_id, status = python_verify_login(number, password)
        print(status)
        print(user_id)
        if status == "success":
            self.destroy()
            app = App(user_id=user_id)
            app.mainloop()
        else:
            self.status_label.configure(text="Invalid credentials. Try again.")


class App(ctk.CTk):
    def __init__(self, user_id=None):
        super().__init__()
        self.title("DENTAL CARE & ORTHODONTICS PATIENT APP")
        self.geometry("1660x1080")

        if user_id is None:
            print("Warning: No user ID provided. Using default.")
            self.user_id = 2  # Default user ID if none is provided
        else:
            self.user_id = user_id
            print(f"User logged in with ID: {self.user_id}")

        self.date = datetime.now().strftime("%Y-%m-%d") # Initialize date to today

        self.title_frame = CTkFrame(self, width=1660, height=100, fg_color="#1B262C")
        self.title_frame.pack(fill="x")
        self.title_label = CTkLabel(self.title_frame, text="MAGNAYE DENTAL CARE & ORTHODONTICS",
                                    text_color="white", font=("Helvetica Neue", 45, "bold"))
        self.title_label.pack(pady=15)

        self.side_frame = CTkFrame(self, width=250, height=980, fg_color="#1A1E23")
        self.side_frame.pack(fill="y", side="left")

        self.create_nav_buttons()
        self.create_profile_section()
        self.update_profile_name()

        self.content_frame = ctk.CTkScrollableFrame(self, width=1410, height=980, fg_color="#222831")
        self.content_frame.pack(fill="both", side="left", expand=True)

        self.show_combined_view()

    def update_profile_name(self):
        try:
            conn = self.get_connection()
            cursor = conn.cursor()
            cursor.execute("SELECT patient_name FROM Patients WHERE patient_id = %s", (self.user_id,))
            result = cursor.fetchone()
            if result:
                self.profile_label.configure(text=result[0])
            else:
                self.profile_label.configure(text="User Profile")
        except mysql.connector.Error as err:
            print("Error loading profile name:", err)
            self.profile_label.configure(text="User Profile")
        finally:
            if hasattr(self, 'cursor') and self.cursor:
                self.cursor.close()
            if hasattr(self, 'conn') and self.conn:
                self.conn.close()

    def get_connection(self):
        return mysql.connector.connect(
            host="localhost",
            port=3308,
            user="root",
            password="",
            database="DentalClinicSystem"
        )
    def create_nav_buttons(self):
        btn_font = ("Arial", 18, "bold")

        self.btn_appointments = CTkButton(self.side_frame, text="Appointments", font=btn_font,
                                          fg_color="#30475E",
                                          command=lambda: self.scroll_to_section(self.appointments_section))
        self.btn_appointments.pack(fill="x", pady=10, padx=10)

        self.btn_history = CTkButton(self.side_frame, text="History Record", font=btn_font,
                                     fg_color="#30475E", command=lambda: self.scroll_to_section(self.history_section))
        self.btn_history.pack(fill="x", pady=10, padx=10)

        self.btn_pricing = CTkButton(self.side_frame, text="Service Pricing", font=btn_font,
                                     fg_color="#30475E", command=lambda: self.scroll_to_section(self.pricing_section))
        self.btn_pricing.pack(fill="x", pady=10, padx=10)

    def create_profile_section(self):
        btn_chatbot = CTkButton(self.side_frame, text="Chat Now", font=("Arial", 18, "bold"),
                                fg_color="#30475E", command=self.open_chatbot)
        btn_chatbot.pack(fill="x", pady=10, padx=10)

        self.profile_frame = CTkFrame(self.side_frame, fg_color="#1A1E23")
        self.profile_frame.pack(side="bottom", fill="x", pady=20, padx=10)

        self.profile_label = CTkLabel(self.profile_frame, text="Loading...", text_color="white",
                                      font=("Arial", 16, "bold"), compound="left")
        self.profile_label.pack(pady=5)

        self.btn_settings = CTkButton(self.profile_frame, text="Settings", fg_color="#30475E",
                                      command=self.show_settings, font=("Arial", 16))
        self.btn_settings.pack(fill="x", pady=5)

    def show_combined_view(self):
        self.clear_content_frame()

        date_label = ctk.CTkLabel(self.content_frame, text="Select Date:", font=("Arial", 20, "bold"),
                                  text_color="white")
        date_label.pack(pady=(20, 5))

        self.date_entry = DateEntry(self.content_frame, width=12, background='darkblue',
                                    foreground='white', borderwidth=2, date_pattern='y-mm-dd')
        self.date_entry.pack()
        self.date_entry.set_date(self.date)  # Set initial date
        self.date_entry.bind("<<DateEntrySelected>>", lambda e: self.refresh_appointments())

        self.current_date_label = ctk.CTkLabel(self.content_frame, text="", font=("Arial", 18), text_color="white")
        self.current_date_label.pack(pady=(5, 20))

        main_container = ctk.CTkFrame(self.content_frame, fg_color="#2B2B2B", corner_radius=10)
        main_container.pack(fill="both", expand=True, padx=10, pady=(0, 10))
        self.appointments_section = ctk.CTkFrame(main_container, fg_color="#2B2B2B", corner_radius=10)
        self.appointments_section.pack(side="top", fill="x", expand=False, pady=(0, 10))

        history_pricing_frame = ctk.CTkFrame(main_container, fg_color="#2B2B2B", corner_radius=10)
        history_pricing_frame.pack(side="bottom", fill="both", expand=True)
        # Load history and pricing in the same row
        self.load_history_and_pricing(history_pricing_frame)
        self.refresh_appointments()

    def load_history_and_pricing(self, parent_frame):
        main_frame = ctk.CTkFrame(parent_frame)
        main_frame.pack(fill="both", expand=True)

        history_frame = ctk.CTkFrame(main_frame)
        history_frame.pack(side="left", fill="both", expand=True, padx=(0, 5), pady=0)
        pricing_frame = ctk.CTkFrame(main_frame)
        pricing_frame.pack(side="right", fill="both", expand=True, padx=(5, 0), pady=0)

        self.load_history(history_frame)
        self.load_pricing(pricing_frame)

    def refresh_appointments(self):
        self.appointments_section.destroy()
        self.appointments_section = CTkFrame(self.content_frame, fg_color="#2B2B2B", corner_radius=10)
        self.appointments_section.pack(fill="x", padx=10, pady=(0, 10))
        self.load_appointments_as_planner(self.appointments_section)

    def load_appointments_as_planner(self, frame):
        time_slots = [
            (datetime.strptime("07:00", "%H:%M") + timedelta(minutes=30 * i)).strftime("%H:%M")
            for i in range(20)
        ]
        planner = defaultdict(lambda: defaultdict(lambda: None))
        selected_date = self.date_entry.get_date()

        try:
            conn = self.get_connection()
            cursor = conn.cursor()
            cursor.callproc('pythonAllAppointments', [self.user_id, selected_date])

            for result in cursor.stored_results():
                for dentist, patient, service, appt_time in result.fetchall():
                    time_str = appt_time.strftime("%H:%M")
                    planner[dentist][time_str] = (patient, service)
        except mysql.connector.Error as err:
            print("Error loading appointments:", err)
            return
        finally:
            if hasattr(self, 'cursor') and self.cursor:
                self.cursor.close()
            if hasattr(self, 'conn') and self.conn:
                self.conn.close()

        scrollable = CTkScrollableFrame(frame, fg_color="transparent")
        scrollable.pack(expand=True, fill="both", padx=10, pady=10)

        dentists = sorted(planner.keys())

        CTkLabel(scrollable, text="Time", font=("Arial", 16, "bold"), text_color="white").grid(row=0, column=0, padx=10, pady=5)
        for col, dentist in enumerate(dentists, start=1):
            CTkLabel(scrollable, text=dentist, font=("Arial", 16, "bold"), text_color="white").grid(row=0, column=col, padx=10, pady=5)

        for row_idx, time in enumerate(time_slots, start=1):
            CTkLabel(scrollable, text=time, font=("Arial", 14), text_color="white").grid(row=row_idx, column=0, padx=5, pady=3)

        for col, dentist in enumerate(dentists, start=1):
            prev_data, start_row, span = None, None, 0

            for row_idx, time in enumerate(time_slots, start=1):
                current_data = planner[dentist].get(time)
                if current_data == prev_data and current_data:
                    span += 1
                else:
                    if prev_data:
                        patient, service = prev_data
                        CTkLabel(scrollable, text=f"{patient}\n{service}", font=("Arial", 13), text_color="white").grid(
                            row=start_row, column=col, rowspan=span, padx=5, pady=3, sticky="nsew")
                    start_row, span = row_idx, 1
                prev_data = current_data

            if prev_data:
                patient, service = prev_data
                CTkLabel(scrollable, text=f"{patient}\n{service}", font=("Arial", 13), text_color="white").grid(
                    row=start_row, column=col, rowspan=span, padx=5, pady=3, sticky="nsew")

    def load_history(self, history_frame):  # Renamed frame argument for clarity
        try:
            conn = self.get_connection()
            cursor = conn.cursor()
            cursor.callproc('pythonHistory', [self.user_id])

            history_label = ctk.CTkLabel(history_frame, text="Appointment History", font=("Arial", 20, "bold"),
                                         text_color="white")
            history_label.pack(pady=10)

            # Create a Treeview widget for the table
            tree = ttk.Treeview(history_frame, columns=("Dentist", "Service", "Cost", "Date"), show="headings")

            # Define column headings
            tree.heading("Dentist", text="Dentist")
            tree.heading("Service", text="Service")
            tree.heading("Cost", text="Cost")
            tree.heading("Date", text="Date")

            # Insert data into the table
            for result in cursor.stored_results():
                for dentist, service, cost, date_done in result.fetchall():
                    tree.insert("", tk.END, values=(dentist, service, cost, date_done))

            tree.pack(padx=20, pady=10, fill="both", expand=True)

        except mysql.connector.Error as err:
            history_error = ctk.CTkLabel(history_frame, text=f"Error loading history: {err}", font=("Arial", 14),
                                         text_color="red")
            history_error.pack(pady=10)
        finally:
            if hasattr(self, 'cursor') and self.cursor:
                self.cursor.close()
            if hasattr(self, 'conn') and self.conn:
                self.conn.close()

    def load_pricing(self, pricing_frame):  # Renamed frame argument for clarity
        pricing_label = ctk.CTkLabel(pricing_frame, text="Service Pricing", font=("Arial", 20, "bold"),
                                     text_color="white")
        pricing_label.pack(pady=10)
        try:
            conn = self.get_connection()
            cursor = conn.cursor()
            cursor.execute("SELECT service_name, duration_minutes, service_cost FROM service")
            services = cursor.fetchall()
            for service, duration, price in services:
                pricing_item = ctk.CTkLabel(pricing_frame, text=f"{service} - {duration} minutes - ₱{price:.2f}",
                                            font=("Arial", 14), text_color="white", anchor="w")
                pricing_item.pack(fill="x", padx=10, pady=2)
        except mysql.connector.Error as err:
            pricing_error = ctk.CTkLabel(pricing_frame, text=f"Error loading pricing: {err}", font=("Arial", 14),
                                         text_color="red")
            pricing_error.pack(pady=10)
        finally:
            if hasattr(self, 'cursor') and self.cursor:
                cursor.close()
            if hasattr(self, 'conn') and self.conn:
                conn.close()



    def show_settings(self):
        self.clear_content_frame()
        title = CTkLabel(self.content_frame, text="Settings", font=("Arial", 30, "bold"), text_color="white")
        title.pack(pady=20)

        options = [
            "\ud83d\udd11 Change Password",
            "\ud83d\udce7 Update Email",
            "\ud83d\udcf1 Update Phone Number",
            "\ud83d\udd14 Notification Preferences",
            "\u274c Log Out"
        ]

        for option in options:
            lbl = CTkLabel(self.content_frame, text=option, font=("Arial", 20), text_color="white")
            lbl.pack(pady=5)

    def scroll_to_section(self, widget):
        y_position = widget.winfo_y() / self.content_frame.winfo_height()
        self.content_frame.yview_moveto(y_position)

    def clear_content_frame(self):
        for widget in self.content_frame.winfo_children():
            widget.destroy()

    def open_chatbot(self):
        chatbot_window = Chatbot(self.user_id)
        chatbot_window.mainloop()

if __name__ == "__main__":
    # Simulate getting user_id from a login page
    # Replace with actual logic from your login page
    app = LoginPage()
    app.mainloop()