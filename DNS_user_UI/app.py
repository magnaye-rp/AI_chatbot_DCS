import customtkinter as ctk
from customtkinter import CTkLabel, CTkButton, CTkFrame, CTkImage
from PIL import Image

class App(ctk.CTk):
    def __init__(self):
        super().__init__()
        self.title("DENTAL CARE & ORTHODONTICS PATIENT APP")
        self.geometry("1660x1080")

        # Title Frame
        self.title_frame = CTkFrame(self, width=1660, height=100, fg_color="#1B262C")
        self.title_frame.pack(fill="x")
        self.title_label = CTkLabel(self.title_frame, text="MAGNAYE DENTAL CARE & ORTHODONTICS",
                                    text_color="white", font=("Helvetica Neue", 45, "bold"))
        self.title_label.pack(pady=15)

        # Sidebar (Navigation)
        self.side_frame = CTkFrame(self, width=250, height=980, fg_color="#1A1E23")
        self.side_frame.pack(fill="y", side="left")

        self.create_nav_buttons()
        self.create_profile_section()

        # Main Content Frame
        self.content_frame = CTkFrame(self, width=1410, height=980, fg_color="#222831")
        self.content_frame.pack(fill="both", side="left", expand=True)

        self.show_appointments()  # Show default view

    def create_nav_buttons(self):
        """Creates sidebar navigation buttons"""
        btn_font = ("Arial", 18, "bold")

        self.btn_appointments = CTkButton(self.side_frame, text="Appointments", font=btn_font,
                                          fg_color="#30475E", command=self.show_appointments)
        self.btn_appointments.pack(fill="x", pady=10, padx=10)

        self.btn_history = CTkButton(self.side_frame, text="History Record", font=btn_font,
                                     fg_color="#30475E", command=self.show_history)
        self.btn_history.pack(fill="x", pady=10, padx=10)

        self.btn_pricing = CTkButton(self.side_frame, text="Service Pricing", font=btn_font,
                                     fg_color="#30475E", command=self.show_pricing)
        self.btn_pricing.pack(fill="x", pady=10, padx=10)

    def create_profile_section(self):
        """Creates a profile info section at the bottom of the sidebar."""
        self.profile_frame = CTkFrame(self.side_frame, fg_color="#1A1E23")
        self.profile_frame.pack(side="bottom", fill="x", pady=20, padx=10)

        # Load user avatar
        #img = Image.open("avatar.png").resize((50, 50))  # Replace with actual image path
        #self.profile_img = CTkImage(light_image=img, dark_image=img, size=(50, 50))

        self.profile_label = CTkLabel(self.profile_frame, text="John Doe", text_color="white",
                                      font=("Arial", 16, "bold"), compound="left")
        self.profile_label.pack(pady=5)

        self.btn_settings = CTkButton(self.profile_frame, text="Settings", fg_color="#30475E",
                                      command=self.show_settings, font=("Arial", 16))
        self.btn_settings.pack(fill="x", pady=5)

    def clear_content_frame(self):
        """Clears the main content frame before displaying new content."""
        for widget in self.content_frame.winfo_children():
            widget.destroy()

    def show_appointments(self):
        """Displays the appointments section."""
        self.clear_content_frame()
        title = CTkLabel(self.content_frame, text="Upcoming Appointments", font=("Arial", 30, "bold"), text_color="white")
        title.pack(pady=20)

        appointments = [
            "🦷 March 20 - 10:00 AM - Dental Cleaning",
            "🦷 March 25 - 2:30 PM - Root Canal",
            "🦷 April 5 - 9:00 AM - Teeth Whitening"
        ]

        for appt in appointments:
            lbl = CTkLabel(self.content_frame, text=appt, font=("Arial", 20), text_color="white")
            lbl.pack(pady=5)

    def show_history(self):
        """Displays patient history records."""
        self.clear_content_frame()
        title = CTkLabel(self.content_frame, text="Patient History", font=("Arial", 30, "bold"), text_color="white")
        title.pack(pady=20)

        history = [
            "✔ February 10 - Tooth Extraction",
            "✔ January 5 - Dental Filling",
            "✔ December 22 - Routine Checkup"
        ]

        for record in history:
            lbl = CTkLabel(self.content_frame, text=record, font=("Arial", 20), text_color="white")
            lbl.pack(pady=5)

    def show_pricing(self):
        """Displays the list of dental services and their prices."""
        self.clear_content_frame()
        title = CTkLabel(self.content_frame, text="Service Pricing", font=("Arial", 30, "bold"), text_color="white")
        title.pack(pady=20)

        services = [
            ("Dental Cleaning", "₱1,500"),
            ("Tooth Extraction", "₱3,000"),
            ("Teeth Whitening", "₱5,500"),
            ("Root Canal", "₱8,000"),
            ("Braces (Per Arch)", "₱25,000")
        ]

        for service, price in services:
            lbl = CTkLabel(self.content_frame, text=f"{service} - {price}", font=("Arial", 20), text_color="white")
            lbl.pack(pady=5)

    def show_settings(self):
        self.clear_content_frame()
        title = CTkLabel(self.content_frame, text="Settings", font=("Arial", 30, "bold"), text_color="white")
        title.pack(pady=20)

        options = [
            "🔑 Change Password",
            "📧 Update Email",
            "📱 Update Phone Number",
            "🔔 Notification Preferences",
            "❌ Log Out"
        ]

        for option in options:
            lbl = CTkLabel(self.content_frame, text=option, font=("Arial", 20), text_color="white")
            lbl.pack(pady=5)

if __name__ == "__main__":
    app = App()
    app.mainloop()
