import customtkinter as ctk
from customtkinter import CTkLabel, CTkButton, CTkFrame, CTkScrollableFrame
from tkcalendar import DateEntry
import tkinter as tk
# AI Chatbot module (re-enable if implemented)
from chatbot import Chatbot
import mysql.connector
from datetime import datetime, timedelta
from collections import defaultdict
import sys

print("PYTHON EXECUTABLE:", sys.executable)

class App(ctk.CTk):
    def __init__(self):
        super().__init__()
        self.title("DENTAL CARE & ORTHODONTICS PATIENT APP")
        self.geometry("1660x1080")

        self.user_id = 2
        self.date = "2025-04-24"

        self.title_frame = CTkFrame(self, width=1660, height=100, fg_color="#1B262C")
        self.title_frame.pack(fill="x")
        self.title_label = CTkLabel(self.title_frame, text="MAGNAYE DENTAL CARE & ORTHODONTICS",
                                    text_color="white", font=("Helvetica Neue", 45, "bold"))
        self.title_label.pack(pady=15)

        self.side_frame = CTkFrame(self, width=250, height=980, fg_color="#1A1E23")
        self.side_frame.pack(fill="y", side="left")

        self.create_nav_buttons()
        self.create_profile_section()

        self.content_frame = ctk.CTkScrollableFrame(self, width=1410, height=980, fg_color="#222831")
        self.content_frame.pack(fill="both", side="left", expand=True)

        self.show_combined_view()

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

        self.profile_label = CTkLabel(self.profile_frame, text="John Doe", text_color="white",
                                      font=("Arial", 16, "bold"), compound="left")
        self.profile_label.pack(pady=5)

        self.btn_settings = CTkButton(self.profile_frame, text="Settings", fg_color="#30475E",
                                      command=self.show_settings, font=("Arial", 16))
        self.btn_settings.pack(fill="x", pady=5)

    def show_combined_view(self):
        self.clear_content_frame()

        date_label = CTkLabel(self.content_frame, text="Select Date:", font=("Arial", 20, "bold"), text_color="white")
        date_label.pack(pady=(20, 5))

        self.date_entry = DateEntry(self.content_frame, width=12, background='darkblue',
                                    foreground='white', borderwidth=2, date_pattern='y-mm-dd')
        self.date_entry.pack()
        self.date_entry.bind("<<DateEntrySelected>>", lambda e: self.refresh_appointments())

        self.current_date_label = CTkLabel(self.content_frame, text="", font=("Arial", 18), text_color="white")
        self.current_date_label.pack(pady=(5, 20))

        self.appointments_section = CTkFrame(self.content_frame, fg_color="#2B2B2B", corner_radius=10)
        self.appointments_section.pack(fill="x", padx=10, pady=(0, 10))

        self.history_section = CTkFrame(self.content_frame, fg_color="#2B2B2B", corner_radius=10)
        self.history_section.pack(fill="x", padx=10, pady=(0, 10))

        self.pricing_section = CTkFrame(self.content_frame, fg_color="#2B2B2B", corner_radius=10)
        self.pricing_section.pack(fill="x", padx=10, pady=(0, 10))

    def refresh_appointments(self):
        self.appointments_section.destroy()
        self.appointments_section = CTkFrame(self.content_frame, fg_color="#2B2B2B", corner_radius=10)
        self.appointments_section.grid(row=0, column=0, sticky="ew", pady=(0, 10))
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
            if cursor:
                cursor.close()
            if conn:
                conn.close()

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

    def load_history(self, frame):
        try:
            conn = self.get_connection()
            cursor = conn.cursor()
            cursor.callproc('pythonAllAppointments', [self.user_id, self.date_entry.get_date()])

            for result in cursor.stored_results():
                for row in result.fetchall():
                    print(row)
        except mysql.connector.Error as err:
            print("Error loading history:", err)
        finally:
            if cursor:
                cursor.close()
            if conn:
                conn.close()

    def load_pricing(self, frame):
        CTkLabel(frame, text="Service Pricing (To be implemented)", font=("Arial", 20), text_color="white").pack(pady=20)

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
        chatbot_window = Chatbot()
        chatbot_window.mainloop()

if __name__ == "__main__":
    app = App()
    app.mainloop()
