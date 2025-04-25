import customtkinter as ctk
from customtkinter import CTkLabel, CTkButton, CTkFrame, CTkScrollableFrame, CTkTextbox, CTkEntry
from tkcalendar import DateEntry
from ai.gui_chatbot import Chatbot
import mysql.connector
from datetime import datetime, timedelta
from collections import defaultdict
import sys
import mysql.connector
import tkinter as tk
from tkinter import ttk

print("PYTHON EXECUTABLE:", sys.executable)

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
        for result in cursor.stored_results():
            row = result.fetchone()
            if row:
                status, user_id = row
                return (status, user_id)  # Always return tuple
        return ("invalid", None)  # Return tuple when no row

    except mysql.connector.Error as err:
        print("Database error:", err)
        return ("error", None)  # Return tuple on error
    finally:
        if 'cursor' in locals() and cursor:
            cursor.close()
        if 'conn' in locals() and conn:
            conn.close()

def python_create_patient(name, number, password):
     try:
         conn = mysql.connector.connect(
             host="localhost",
             port=3308,
             user="root",
             password="",
             database="DentalClinicSystem"
         )
         cursor = conn.cursor()
         cursor.callproc('python_create_patient', [name, number, password])
         conn.commit()
         return True
     except mysql.connector.Error as err:
         print("Database error:", err)
         return False
     finally:
         if cursor:
             cursor.close()
         if conn:
             conn.close()

class SignUpDialog(ctk.CTkToplevel):
     def __init__(self, *args, **kwargs):
         super().__init__(*args, **kwargs)
         self.title("Sign Up")

         self.name_label = CTkLabel(self, text="Full Name:")
         self.name_label.pack(pady=5, padx=20)
         self.name_entry = ctk.CTkEntry(self)
         self.name_entry.pack(pady=5, padx=20)

         self.phone_label = CTkLabel(self, text="Phone Number:")
         self.phone_label.pack(pady=5, padx=20)
         self.phone_entry = ctk.CTkEntry(self)
         self.phone_entry.pack(pady=5, padx=20)

         self.password_label = CTkLabel(self, text="Password:")
         self.password_label.pack(pady=5, padx=20)
         self.password_entry = ctk.CTkEntry(self, show="*")
         self.password_entry.pack(pady=5, padx=20)

         self.signup_button = CTkButton(self, text="Sign Up", command=self.signup_action)
         self.signup_button.pack(pady=10, padx=20)

         self.status_label = CTkLabel(self, text="", text_color="green")
         self.status_label.pack()

     def signup_action(self):
         name = self.name_entry.get()
         number = self.phone_entry.get()
         password = self.password_entry.get()

         if python_create_patient(name, number, password):
             self.status_label.configure(text="Sign up successful!")
             self.after(1500, self.destroy) # Close dialog after a short delay
         else:
             self.status_label.configure(text="Sign up failed. Please try again.", text_color="red")

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
         self.login_button.pack(pady=10)

         self.signup_button = CTkButton(self, text="Sign Up", command=self.open_signup_dialog)
         self.signup_button.pack(pady=5)

         self.status_label = CTkLabel(self, text="", text_color="red")
         self.status_label.pack()

     def try_login(self):
         number = self.phone_entry.get()
         password = self.password_entry.get()
         login_result = python_verify_login(number, password)
         print(login_result[0])
         if login_result and login_result[0] == "success":
             self.destroy()
             run_app(user_id)
         else:
             self.status_label.configure(text="Invalid credentials. Try again.")

     def open_signup_dialog(self):
         signup_dialog = SignUpDialog(self)
         signup_dialog.grab_set() # Make the dialog modal

class App(ctk.CTk):
     def __init__(self, user_id=None):
         super().__init__()
         self.title("DENTAL CARE & ORTHODONTICS PATIENT APP")
         self.geometry("1400x800")  # Fixed main window size

         if user_id is None:
             print("Warning: No user ID provided. Using default.")
             self.user_id = 2
         else:
             self.user_id = user_id
             print(f"User logged in with ID: {self.user_id}")

         self.date = datetime.now().strftime("%Y-%m-%d")

         self.title_frame = CTkFrame(self, width=1400, height=80, fg_color="#1B262C")  # Adjusted height
         self.title_frame.pack(fill="x")
         self.title_label = CTkLabel(self.title_frame, text="MAGNAYE DENTAL CARE & ORTHODONTICS",
                                     text_color="white", font=("Helvetica Neue", 30, "bold"))  # Adjusted font size
         self.title_label.pack(pady=10)

         self.side_frame = CTkFrame(self, width=200, height=800, fg_color="#1A1E23")  # Fixed sidebar width and height
         self.side_frame.pack(fill="y", side="left")

         self.create_nav_buttons()
         self.create_profile_section()

         self.content_frame = ctk.CTkFrame(self, width=1200, height=800, fg_color="#222831")  # Fixed content frame size
         self.content_frame.pack(fill="both", side="left", expand=True)

         self.show_dashboard_view()

     def create_nav_buttons(self):
         btn_font = ("Arial", 16, "bold")  # Adjusted font size

         self.btn_home = CTkButton(self.side_frame, text="Home", font=btn_font,
                                   fg_color="#30475E", command=self.show_dashboard_view)
         self.btn_home.pack(fill="x", pady=5, padx=5)  # Adjusted padding

     def create_profile_section(self):
         btn_chatbot = CTkButton(self.side_frame, text="Chat Now", font=("Arial", 16, "bold"),
                                 fg_color="#30475E", command=self.open_chatbot_view)
         btn_chatbot.pack(fill="x", pady=5, padx=5)

         self.profile_frame = CTkFrame(self.side_frame, fg_color="#1A1E23")
         self.profile_frame.pack(side="bottom", fill="x", pady=10, padx=5)

         self.profile_label = CTkLabel(self.profile_frame, text="Loading...", text_color="white",
                                       font=("Arial", 14, "bold"), compound="left")
         self.profile_label.pack(pady=3)

         self.btn_settings = CTkButton(self.profile_frame, text="Settings", fg_color="#30475E",
                                       command=self.show_settings, font=("Arial", 14))
         self.btn_settings.pack(fill="x", pady=3)

     def show_dashboard_view(self):
         self.clear_content_frame()

         date_label = ctk.CTkLabel(self.content_frame, text="Select Date:", font=("Arial", 18, "bold"),
                                   text_color="white")
         date_label.pack(pady=(10, 5))

         self.date_entry = DateEntry(self.content_frame, width=12, background='darkblue',
                                     foreground='white', borderwidth=2, date_pattern='y-mm-dd')
         self.date_entry.pack()
         self.date_entry.set_date(self.date)  # Set initial date
         self.date_entry.bind("<<DateEntrySelected>>", lambda e: self.refresh_appointments())

         self.current_date_label = ctk.CTkLabel(self.content_frame, text="", font=("Arial", 16), text_color="white")
         self.current_date_label.pack(pady=(5, 10))

         main_container = ctk.CTkFrame(self.content_frame, fg_color="#2B2B2B", corner_radius=10)
         main_container.pack(fill="both", expand=True, padx=10, pady=(0, 10))

         self.appointments_section = ctk.CTkFrame(main_container, fg_color="#2B2B2B", corner_radius=10)
         self.appointments_section.pack(side="top", fill="x", expand=False, pady=(0, 10))

         self.load_appointments_as_planner(self.appointments_section)

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

             for dentist, patient, service, appt_time in cursor:
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

         for row_idx, time in enumerate(time_slots, start=1):
             CTkLabel(frame, text=time, font=("Arial", 12), text_color="white").grid(row=row_idx, column=0, padx=3, pady=2)

         dentists = sorted(planner.keys())
         for col, dentist in enumerate(dentists, start=1):
             prev_data, start_row, span = None, None, 0
             for row_idx, time in enumerate(time_slots, start=1):
                 current_data = planner[dentist].get(time)
                 if current_data == prev_data and current_data:
                     span += 1
                 else:
                     if prev_data:
                         patient, service = prev_data
                         CTkLabel(frame, text=f"{patient}\n{service}", font=("Arial", 11), text_color="white").grid(
                             row=start_row, column=col, rowspan=span, padx=3, pady=2, sticky="nsew")
                     start_row, span = row_idx, 1
                 prev_data = current_data

             if prev_data:
                 patient, service = prev_data
                 CTkLabel(frame, text=f"{patient}\n{service}", font=("Arial", 11), text_color="white").grid(
                     row=start_row, column=col, rowspan=span, padx=3, pady=2, sticky="nsew")

     def show_settings(self):
         self.clear_content_frame()
         title = CTkLabel(self.content_frame, text="Settings", font=("Arial", 24, "bold"), text_color="white")  # Adjusted font size
         title.pack(pady=15)

         options = [
             "\ud83d\udd11 Change Password",
             "\ud83d\udce7 Update Email",
             "\ud83d\udcf1 Update Phone Number",
             "\ud83d\udd14 Notification Preferences",
             "\u274c Log Out"
         ]

         for option in options:
             lbl = CTkLabel(self.content_frame, text=option, font=("Arial", 16), text_color="white")
             lbl.pack(pady=3)

     def clear_content_frame(self):
         for widget in self.content_frame.winfo_children():
             widget.destroy()

     def get_connection(self):
         try:
             return mysql.connector.connect(
                 host="localhost",
                 port=3308,
                 user="root",
                 password="",
                 database="DentalClinicSystem"
             )
         except mysql.connector.Error as err:
             print(f"Database connection error: {err}")
             raise
     def clear_content_frame(self):
         for widget in self.content_frame.winfo_children():
             widget.destroy()

     def open_chatbot_view(self):
         self.clear_content_frame()
         self.chatbot_instance = Chatbot(self.user_id, self.display_bot_response)
         self.chat_display = ctk.CTkTextbox(self.content_frame,
                                            state="disabled",
                                            height=400,
                                            wrap="word")  # Added word wrap
         self.chat_display.pack(padx=10, pady=10, fill="both", expand=True)

         self.chat_display.tag_config("user", foreground="black")
         self.chat_display.tag_config("bot", foreground="green")

         self.input_entry = ctk.CTkEntry(self.content_frame,
                                         placeholder_text="Type your message...")
         self.input_entry.pack(padx=10, pady=(0, 10), fill="x")
         self.input_entry.bind("<Return>", self.send_message)

         self.send_button = ctk.CTkButton(self.content_frame,
                                          text="Send",
                                          command=self.send_message)
         self.send_button.pack(padx=10, pady=(0, 10), anchor="e")

         self.add_bot_message("Hello! How can I help you today?")

     def send_message(self, event=None):
         user_input = self.input_entry.get().strip()
         if user_input and self.chatbot_instance:
             self.add_user_message(f"You: {user_input}")
             self.input_entry.delete(0, "end")
             self.chatbot_instance.run_chat(user_input)

     def add_user_message(self, message):
         self.chat_display.configure(state="normal")
         self.chat_display.insert("end", message + "\n", "user")
         self.chat_display.configure(state="disabled")
         self.chat_display.see("end")

     def add_bot_message(self, message):
         self.chat_display.configure(state="normal")
         self.chat_display.insert("end", message + "\n", "bot")
         self.chat_display.configure(state="disabled")
         self.chat_display.see("end")

     def display_bot_response(self, message):
         self.add_bot_message(f"Bot: {message}")


def run_app(user_id):
         app = App(user_id)
         app.mainloop()

if __name__ == "__main__":
    user_id = 1
    run_app(user_id)