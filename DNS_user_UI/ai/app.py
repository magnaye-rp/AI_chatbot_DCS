import customtkinter as ctk
from customtkinter import CTkLabel, CTkButton, CTkFrame, CTkScrollableFrame, CTkTextbox, CTkEntry, CTkToplevel
from tkcalendar import DateEntry
from ai.gui_chatbot import Chatbot, ChatGUIApp
import mysql.connector
from datetime import datetime, timedelta
from collections import defaultdict
import sys
import tkinter as tk
from tkinter import ttk
from customtkinter import CTkComboBox, CTkOptionMenu, CTkButton

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
                return (status, user_id)  # Return proper tuple with user_id
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
        self.geometry("400x300")  # Set consistent size

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
            self.after(1500, self.destroy)
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
            # Fixed: Now properly passing user_id from the tuple
            run_app(login_result[1])
        else:
            self.status_label.configure(text="Invalid credentials. Try again.")

    def open_signup_dialog(self):
        signup_dialog = SignUpDialog(self)
        signup_dialog.grab_set()  # Make the dialog modal

def getname(query, params):
    conn = mysql.connector.connect(
        host="localhost",
        port=3308,
        user="root",
        password="",
        database="DentalClinicSystem"
    )
    cursor = conn.cursor()
    cursor.execute(query, params)
    result = cursor.fetchone()
    cursor.close()
    conn.close()
    return result

class App(ctk.CTk):
    active_chatbot = 0
    open = 0

    def __init__(self, user_id=None):
        super().__init__()
        self.title("DENTAL CARE & ORTHODONTICS PATIENT APP")
        self.geometry("1400x800")

        if user_id is None:
            print("Warning: No user ID provided. Using default.")
            self.user_id = 2
        else:
            self.user_id = user_id
            print(f"User logged in with ID: {self.user_id}")

        self.date = datetime.now().strftime("%Y-%m-%d")

        self.create_layout()

        self.show_dashboard_view()

    def create_layout(self):
        self.title_frame = CTkFrame(self, height=80, fg_color="#1B262C")
        self.title_frame.pack(fill="x")
        self.title_label = CTkLabel(self.title_frame, text="MAGNAYE DENTAL CARE & ORTHODONTICS",
                                    text_color="white", font=("Helvetica Neue", 30, "bold"))
        self.title_label.pack(pady=10)

        self.main_container = CTkFrame(self, fg_color="#222831")
        self.main_container.pack(fill="both", expand=True)

        self.side_frame = CTkFrame(self.main_container, width=200, fg_color="#1A1E23")
        self.side_frame.pack(fill="y", side="left")
        self.side_frame.pack_propagate(False)

        self.create_nav_buttons()
        self.create_profile_section()

        self.content_outer_frame = CTkFrame(self.main_container, fg_color="#222831")
        self.content_outer_frame.pack(fill="both", side="left", expand=True)

        self.content_frame = None

    def create_nav_buttons(self):
        btn_font = ("Arial", 16, "bold")

        self.btn_home = CTkButton(self.side_frame, text="Home", font=btn_font,
                                  fg_color="#30475E", command=self.show_dashboard_view)
        self.btn_home.pack(fill="x", pady=5, padx=5)

    def create_profile_section(self):
        btn_chatbot = CTkButton(self.side_frame, text="Chat Now", font=("Arial", 16, "bold"),
                                fg_color="#30475E", command=self.open_chatbot_view)
        btn_chatbot.pack(fill="x", pady=5, padx=5)

        self.profile_frame = CTkFrame(self.side_frame, fg_color="#1A1E23")
        self.profile_frame.pack(side="bottom", fill="x", pady=10, padx=5)

        # Get full name of the user
        result = getname("SELECT full_name FROM patient WHERE patient_id = %s", (self.user_id,))
        full_name = result[0] if result else "Guest"

        self.profile_label = CTkLabel(self.profile_frame, text=full_name, text_color="white",
                                      font=("Arial", 14, "bold"), compound="left")
        self.profile_label.pack(pady=3)

        self.btn_settings = CTkButton(self.profile_frame, text="Settings", fg_color="#30475E",
                                      command=self.show_settings, font=("Arial", 14))
        self.btn_settings.pack(fill="x", pady=3)

    def clear_content_frame(self):
        if self.content_frame:
            self.content_frame.destroy()
            self.content_frame = None

        for widget in self.content_outer_frame.winfo_children():
            widget.destroy()

        self.content_frame = CTkScrollableFrame(self.content_outer_frame, fg_color="#222831")
        self.content_frame.pack(fill="both", expand=True, padx=10, pady=10)

    def show_dashboard_view(self):
        self.clear_content_frame()

        time_choices = []
        for hour in range(8, 18):  # From 8:00 AM to 5:30 PM
            time_choices.append(f"{hour:02d}:00")
            time_choices.append(f"{hour:02d}:30")

        date_container = CTkFrame(self.content_frame, fg_color="#2B2B2B")
        date_container.pack(fill="x", pady=10)

        time_choices = [f"{h:02d}:{m:02d}" for h in range(8, 18) for m in (0, 30)]
        services = ["Dental Cleaning",
                    "Tooth Extraction",
                    "Tooth Filling",
                    "Root Canal Treatment",
                    "Braces Adjustment",
                    "Teeth Whitening",
                    "Dental X-Ray",
                    "Consultation"
                    ]

        date_label = CTkLabel(date_container, text="Select Date:", font=("Arial", 18, "bold"), text_color="white")
        date_label.pack(side="left", padx=(10, 5), pady=10)

        self.date_entry = DateEntry(date_container, width=12, background='darkblue',
                                    foreground='white', borderwidth=2, date_pattern='y-mm-dd')
        self.date_entry.pack(side="left", padx=5, pady=10)
        self.date_entry.set_date(self.date)
        self.date_entry.bind("<<DateEntrySelected>>", lambda e: self.refresh_appointments())

        # Time chooser
        self.time_combo = CTkComboBox(date_container, values=time_choices, width=100)
        self.time_combo.set("08:00")
        self.time_combo.pack(side="left", padx=5, pady=10)

        # Service selector
        self.service_menu = CTkOptionMenu(date_container, values=services)
        self.service_menu.set("Dental Cleaning")
        self.service_menu.pack(side="left", padx=5, pady=10)

        def book_now():
            selected_date = self.date_entry.get_date().strftime("%Y-%m-%d")
            selected_time = self.time_combo.get()
            selected_service = self.service_menu.get()

            try:
                conn = mysql.connector.connect(
                    host="localhost",
                    port=3308,
                    user="root",
                    password="",
                    database="DentalClinicSystem"
                )
                cursor = conn.cursor()
                try:
                    cursor.callproc('python_book_appointment',
                                    [self.user_id, selected_date, selected_time, selected_service])

                    # Collect result from stored procedure
                    result_message = None
                    for result_cursor in cursor.stored_results():
                        results = result_cursor.fetchall()
                        if results and len(results[0]) > 0:
                            result_message = results[0][0]  # Assuming message is the first column

                    conn.commit()

                    # Check the message content
                    if result_message and "success" in result_message.lower():
                        self.show_popup("Success", result_message)
                    else:
                        self.show_popup("Failed", result_message or "Unknown error occurred.")

                except mysql.connector.Error as err:
                    conn.rollback()
                    self.show_popup("Error", f"MySQL Error:\n{err}")

                except Exception as e:
                    conn.rollback()
                    self.show_popup("Error", f"Unexpected Error:\n{str(e)}")

                finally:
                    cursor.close()
                    conn.close()

            except mysql.connector.Error as conn_err:
                self.show_popup("Connection Error", str(conn_err))

        book_btn = CTkButton(date_container, text="Book Appointment", command=book_now)
        book_btn.pack(side="left", padx=10, pady=10)

        # Optional: Label for extra display
        self.current_date_label = CTkLabel(date_container, text="", font=("Arial", 16), text_color="white")
        self.current_date_label.pack(side="left", padx=10, pady=10)

        top_container = CTkFrame(self.content_frame, fg_color="#222831")
        top_container.pack(fill="both", expand=True, pady=(10, 5))

        bottom_container = CTkFrame(self.content_frame, fg_color="#222831")
        bottom_container.pack(fill="both", expand=True, pady=(5, 10))

        # Use grid layout inside top_container
        top_container.columnconfigure(0, weight=82)  # planner = 65%
        top_container.columnconfigure(1, weight=18)  # pricing = 35%
        top_container.rowconfigure(0, weight=1)

        # Planner Section (left side)
        planner_container = CTkFrame(top_container, fg_color="#2B2B2B", corner_radius=10)
        planner_container.grid(row=0, column=0, sticky="nsew", padx=(0, 0), pady=0)

        # Pricing Section (right side)
        pricing_container = CTkFrame(top_container, fg_color="#2B2B2B", corner_radius=10)
        pricing_container.grid(row=0, column=1, sticky="nsew", padx=(5, 0), pady=0)

        history_container = CTkFrame(bottom_container, fg_color="#2B2B2B", corner_radius=10)
        history_container.pack(side="left", fill="both", expand=True, padx=(0, 5), pady=0)

        # Current appointments section (45% of bottom)
        appointments_container = CTkFrame(bottom_container, fg_color="#2B2B2B", corner_radius=10)
        appointments_container.pack(side="right", fill="both", expand=True, padx=(5, 0), pady=0)

        # Create scrollable sections for each container
        self.appointments_section = CTkScrollableFrame(planner_container, fg_color="#2B2B2B",
                                                       corner_radius=10, height=300)
        self.appointments_section.pack(fill="both", expand=True, padx=5, pady=5)

        # Load data into each section
        self.load_appointments_as_planner(self.appointments_section)
        self.load_pricing(pricing_container)
        self.load_history(history_container)
        self.load_appointments(appointments_container)

    def refresh_appointments(self):
        for widget in self.appointments_section.winfo_children():
            widget.destroy()
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
                for dentist, patient, service, appt_time in result:
                    time_str = appt_time.strftime("%H:%M")
                    planner[dentist][time_str] = (patient, service)
        except mysql.connector.Error as err:
            print("Error loading appointments:", err)
            return
        finally:
            if 'cursor' in locals() and cursor:
                cursor.close()
            if 'conn' in locals() and conn:
                conn.close()

        planner_grid = CTkFrame(frame, fg_color="#2B2B2B")
        planner_grid.pack(fill="both", expand=True)

        dentists = sorted(planner.keys())
        CTkLabel(planner_grid, text="Time", font=("Arial", 14, "bold"),
                 text_color="white").grid(row=0, column=0, padx=3, pady=2, sticky="w")

        for col, dentist in enumerate(dentists, start=1):
            CTkLabel(planner_grid, text=dentist, font=("Arial", 14, "bold"),
                     text_color="white").grid(row=0, column=col, padx=3, pady=2)

        # Add time slots and appointments
        for row_idx, time in enumerate(time_slots, start=1):
            CTkLabel(planner_grid, text=time, font=("Arial", 12),
                     text_color="white").grid(row=row_idx, column=0, padx=3, pady=2, sticky="w")

        for col, dentist in enumerate(dentists, start=1):
            prev_data, start_row, span = None, None, 0
            for row_idx, time in enumerate(time_slots, start=1):
                current_data = planner[dentist].get(time)
                if current_data == prev_data and current_data:
                    span += 1
                else:
                    if prev_data:
                        patient, service = prev_data
                        appointment_frame = CTkFrame(planner_grid, fg_color="#30475E", corner_radius=5)
                        appointment_frame.grid(row=start_row, column=col, rowspan=span, padx=3, pady=2, sticky="nsew")
                        CTkLabel(appointment_frame, text=f"{patient}", font=("Arial", 12, "bold"),
                                 text_color="white").pack(pady=(5, 2))
                        CTkLabel(appointment_frame, text=f"{service}", font=("Arial", 11),
                                 text_color="white").pack(pady=(0, 5))
                    start_row, span = row_idx, 1
                prev_data = current_data

            if prev_data:
                patient, service = prev_data
                appointment_frame = CTkFrame(planner_grid, fg_color="#30475E", corner_radius=5)
                appointment_frame.grid(row=start_row, column=col, rowspan=span, padx=3, pady=2, sticky="nsew")
                CTkLabel(appointment_frame, text=f"{patient}", font=("Arial", 12, "bold"),
                         text_color="white").pack(pady=(5, 2))
                CTkLabel(appointment_frame, text=f"{service}", font=("Arial", 11),
                         text_color="white").pack(pady=(0, 5))

    def load_history_and_pricing(self, parent_frame):
        main_frame = CTkFrame(parent_frame, fg_color="#2B2B2B")  # Fixed: added fg_color
        main_frame.pack(fill="both", expand=True, padx=5, pady=5)  # Fixed: added padding

        # Create two side-by-side frames
        history_frame = CTkFrame(main_frame, fg_color="#30475E",
                                 corner_radius=5)  # Fixed: added fg_color and corner_radius
        history_frame.pack(side="left", fill="both", expand=True, padx=(0, 5), pady=0)

        pricing_frame = CTkFrame(main_frame, fg_color="#30475E",
                                 corner_radius=5)  # Fixed: added fg_color and corner_radius
        pricing_frame.pack(side="right", fill="both", expand=True, padx=(5, 0), pady=0)

        self.load_history(history_frame)
        self.load_pricing(pricing_frame)

    def load_history(self, history_frame):
        try:
            conn = self.get_connection()
            cursor = conn.cursor()
            cursor.callproc('pythonHistory', [self.user_id])

            history_label = CTkLabel(history_frame, text="Appointment History", font=("Arial", 20, "bold"),
                                     text_color="white")
            history_label.pack(pady=10)

            # Create a scrollable frame for the history table
            history_scroll_frame = CTkScrollableFrame(history_frame,
                                                      fg_color="#30475E")  # Fixed: added scrollable container
            history_scroll_frame.pack(padx=10, pady=10, fill="both", expand=True)

            # Create a Treeview widget for the table
            style = ttk.Style()
            style.configure("Treeview", background="#30475E", foreground="white", fieldbackground="#30475E")
            style.map('Treeview', background=[('selected', '#1B262C')])

            tree = ttk.Treeview(history_scroll_frame, columns=("Dentist", "Service", "Cost", "Date"), show="headings",
                                height=8)

            # Define column headings and widths
            tree.heading("Dentist", text="Dentist")
            tree.heading("Service", text="Service")
            tree.heading("Cost", text="Cost")
            tree.heading("Date", text="Date")

            tree.column("Dentist", width=100)
            tree.column("Service", width=150)
            tree.column("Cost", width=70)
            tree.column("Date", width=100)

            # Insert data into the table
            for result in cursor.stored_results():
                for dentist, service, cost, date_done in result.fetchall():
                    formatted_date = date_done.strftime("%Y-%m-%d") if isinstance(date_done, datetime) else date_done
                    formatted_cost = f"₱{float(cost):.2f}" if cost else "N/A"
                    tree.insert("", tk.END, values=(dentist, service, formatted_cost, formatted_date))

            tree.pack(fill="both", expand=True)

        except mysql.connector.Error as err:
            history_error = CTkLabel(history_frame, text=f"Error loading history: {err}", font=("Arial", 14),
                                     text_color="red")
            history_error.pack(pady=10)
        finally:
            if 'cursor' in locals() and cursor:
                cursor.close()
            if 'conn' in locals() and conn:
                conn.close()

    def load_pricing(self, pricing_frame):
        pricing_label = CTkLabel(pricing_frame, text="Service Pricing", font=("Arial", 20, "bold"),
                                 text_color="white")
        pricing_label.pack(pady=10)

        pricing_scroll_frame = CTkScrollableFrame(pricing_frame, fg_color="#30475E")
        pricing_scroll_frame.pack(padx=10, pady=10, fill="both", expand=True)

        try:
            conn = self.get_connection()
            cursor = conn.cursor()
            cursor.execute("SELECT service_name, duration_minutes, service_cost FROM service")
            services = cursor.fetchall()

            for service, duration, price in services:
                service_frame = CTkFrame(pricing_scroll_frame, fg_color="#1B262C", corner_radius=5)
                service_frame.pack(fill="x", pady=3, padx=5, ipady=5)

                price_formatted = f"₱{float(price):.2f}" if price else "N/A"
                pricing_item = CTkLabel(service_frame,
                                        text=f"{service} - {duration} min - {price_formatted}",
                                        font=("Arial", 14), text_color="white", anchor="w")
                pricing_item.pack(fill="x", padx=10)
        except mysql.connector.Error as err:
            pricing_error = CTkLabel(pricing_scroll_frame, text=f"Error loading pricing: {err}", font=("Arial", 14),
                                     text_color="red")
            pricing_error.pack(pady=10)
        finally:
            if 'cursor' in locals() and cursor:
                cursor.close()
            if 'conn' in locals() and conn:
                conn.close()

    def load_appointments(self, frame):
        try:
            conn = self.get_connection()
            cursor = conn.cursor()
            cursor.callproc('python_user_appointments', [self.user_id])

            title = CTkLabel(frame, text="Current Appointments", font=("Arial", 20, "bold"),
                             text_color="white")
            title.pack(pady=10)

            # Create a scrollable frame for the appointments table
            scroll_frame = CTkScrollableFrame(frame, fg_color="#30475E")
            scroll_frame.pack(padx=10, pady=10, fill="both", expand=True)

            # Create a Treeview widget for the table
            style = ttk.Style()
            style.configure("Treeview", background="#30475E", foreground="white", fieldbackground="#30475E")
            style.map('Treeview', background=[('selected', '#1B262C')])

            tree = ttk.Treeview(scroll_frame, columns=("Dentist", "Service", "Date"), show="headings", height=8)

            # Define column headings and widths
            tree.heading("Dentist", text="Dentist")
            tree.heading("Service", text="Service")
            tree.heading("Date", text="Date")

            tree.column("Dentist", width=100)
            tree.column("Service", width=150)
            tree.column("Date", width=100)

            # Insert data into the table
            for result in cursor.stored_results():
                for dentist, service, date_done in result.fetchall():
                    formatted_date = date_done.strftime("%Y-%m-%d") if isinstance(date_done, datetime) else date_done
                    tree.insert("", tk.END, values=(dentist, service, formatted_date))

            tree.pack(fill="both", expand=True)

        except mysql.connector.Error as err:
            error = CTkLabel(frame, text=f"Error loading appointments: {err}", font=("Arial", 14),
                             text_color="red")
            error.pack(pady=10)
        finally:
            if 'cursor' in locals() and cursor:
                cursor.close()
            if 'conn' in locals() and conn:
                conn.close()

    def show_popup(self, title, message):
        popup = CTkToplevel(self)
        popup.title(title)
        popup.geometry("350x160")
        popup.grab_set()

        CTkLabel(popup, text=message, font=("Arial", 14), text_color="white",
                 wraplength=300, justify="center").pack(pady=20)
        CTkButton(popup, text="OK", command=popup.destroy).pack(pady=10)

    def show_settings(self):
        self.clear_content_frame()

        title = CTkLabel(self.content_frame, text="Account Settings", font=("Arial", 24, "bold"), text_color="white")
        title.pack(pady=15)

        settings_container = CTkFrame(self.content_frame, fg_color="#2B2B2B", corner_radius=10)
        settings_container.pack(fill="both", expand=True, padx=20, pady=10)

        # Create a form container
        form_frame = CTkFrame(settings_container, fg_color="#30475E", corner_radius=8)
        form_frame.pack(fill="both", expand=True, padx=20, pady=20)

        # Create entry variables
        self.full_name_var = tk.StringVar()
        self.contact_num_var = tk.StringVar()
        self.address_var = tk.StringVar()
        self.password_var = tk.StringVar()

        # Label and entry for full name
        name_label = CTkLabel(form_frame, text="Full Name:", font=("Arial", 16, "bold"), text_color="white")
        name_label.grid(row=0, column=0, padx=10, pady=(20, 5), sticky="w")
        name_entry = CTkEntry(form_frame, textvariable=self.full_name_var, width=400, height=40)
        name_entry.grid(row=0, column=1, padx=10, pady=(20, 5), sticky="ew")

        # Label and entry for contact number
        contact_label = CTkLabel(form_frame, text="Contact Number:", font=("Arial", 16, "bold"), text_color="white")
        contact_label.grid(row=1, column=0, padx=10, pady=5, sticky="w")
        contact_entry = CTkEntry(form_frame, textvariable=self.contact_num_var, width=400, height=40)
        contact_entry.grid(row=1, column=1, padx=10, pady=5, sticky="ew")

        # Label and entry for address
        address_label = CTkLabel(form_frame, text="Address:", font=("Arial", 16, "bold"), text_color="white")
        address_label.grid(row=2, column=0, padx=10, pady=5, sticky="w")
        address_entry = CTkEntry(form_frame, textvariable=self.address_var, width=400, height=40)
        address_entry.grid(row=2, column=1, padx=10, pady=5, sticky="ew")

        # Label and entry for password
        password_label = CTkLabel(form_frame, text="Password:", font=("Arial", 16, "bold"), text_color="white")
        password_label.grid(row=3, column=0, padx=10, pady=5, sticky="w")
        password_entry = CTkEntry(form_frame, textvariable=self.password_var, width=400, height=40, show="•")
        password_entry.grid(row=3, column=1, padx=10, pady=5, sticky="ew")

        # Configure the grid to expand properly
        form_frame.grid_columnconfigure(1, weight=1)

        # Load data from database
        try:
            conn = self.get_connection()
            cursor = conn.cursor()
            query = "SELECT `full_name`, `contact_num`, `address`, `password` FROM `patient` WHERE `patient_id` = %s"
            cursor.execute(query, (self.user_id,))
            result = cursor.fetchone()

            if result:
                self.full_name_var.set(result[0])
                self.contact_num_var.set(result[1])
                self.address_var.set(result[2])
                self.password_var.set(result[3])

            cursor.close()
            conn.close()

        except mysql.connector.Error as err:
            error_label = CTkLabel(settings_container, text=f"Database Error: {err}", font=("Arial", 16),
                                   text_color="red")
            error_label.pack(pady=10)

        # Create update button
        def update_profile():
            try:
                conn = self.get_connection()
                cursor = conn.cursor()

                update_query = """
                UPDATE patient 
                SET full_name = %s, contact_num = %s, address = %s, password = %s 
                WHERE patient_id = %s
                """

                cursor.execute(update_query, (
                    self.full_name_var.get(),
                    self.contact_num_var.get(),
                    self.address_var.get(),
                    self.password_var.get(),
                    self.user_id
                ))

                conn.commit()
                cursor.close()
                conn.close()

                # Update the profile label in the sidebar
                self.profile_label.configure(text=self.full_name_var.get())

                # Show success message
                self.show_popup("Success", "Your profile has been updated successfully!")

            except mysql.connector.Error as err:
                self.show_popup("Error", f"Failed to update profile: {err}")

        # Button frame
        button_frame = CTkFrame(form_frame, fg_color="#30475E")
        button_frame.grid(row=4, column=0, columnspan=2, pady=20)

        update_button = CTkButton(button_frame, text="Update Profile",
                                  font=("Arial", 16, "bold"),
                                  fg_color="#1B262C", hover_color="#0F4C75",
                                  command=update_profile,
                                  width=200, height=50)
        update_button.pack(pady=10)

        # Add a status message label below the update button
        self.status_label = CTkLabel(form_frame, text="", font=("Arial", 14), text_color="#4CC9F0")
        self.status_label.grid(row=5, column=0, columnspan=2, pady=10)

    def getname(query, params):
        conn = mysql.connector.connect( host="localhost",
                port=3308,
                user="root",
                password="",
                database="DentalClinicSystem")
        cursor = conn.cursor()
        cursor.execute(query, params)
        result = cursor.fetchone()
        cursor.close()
        conn.close()
        return result

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

    def open_chatbot_view(self):
        active_chatbot = 0
        if active_chatbot == 0:
            root = ctk.CTkToplevel()
            root.geometry("800x600")
            ChatGUIApp(root, self.user_id)


def run_app(user_id):
    app = App(user_id)
    app.mainloop()

if __name__ == "__main__":
    app = LoginPage()
    app.mainloop()