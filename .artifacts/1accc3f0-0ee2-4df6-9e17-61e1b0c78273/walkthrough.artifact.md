# Fluxion App Walkthrough

Fluxion is a cutting-edge smart energy management application designed to give users real-time control and deep insights into their electricity consumption. Built with the latest Android technologies, it offers a seamless, high-performance experience for monitoring smart meters, managing balances, and optimizing energy efficiency.

## 🚀 Key Features

### 1. Live Energy Monitoring
The heart of Fluxion is the **Dashboard**, which provides a real-time feed of energy usage.
- **Dynamic Usage Chart**: A custom-drawn, real-time animated chart that tracks instantaneous power consumption in Watts.
- **Smart Metrics**: At-a-glance cards showing Current Balance, Today's Usage (kWh), Current Power (W), and Voltage (V).
- **Connection Status**: Instant visibility of the smart meter's online/offline status.

### 2. Smart Analytics & Insights
The **Analytics** screen transforms raw data into actionable knowledge.
- **Consumption Trends**: Toggle between Daily, Weekly, and Monthly views to understand long-term usage patterns.
- **Appliance Breakdown**: An AI-powered breakdown showing which devices (AC, Fridge, Lighting, etc.) are consuming the most energy.
- **AI Insights**: Proactive suggestions and alerts, such as saving targets and high-usage warnings, to help users reduce their carbon footprint and bills.

### 3. Financial Management
The **Recharge** system ensures that users never run out of energy.
- **Quick Recharge**: A streamlined flow to add credit to the smart meter wallet.
- **Transaction History**: A detailed list of recent activity, including recharges and automated bill payments.

### 4. Seamless Navigation & Profile
A centralized hub for account management and notifications.
- **Real-time Notifications**: Alerts for low balance, high usage, or system updates.
- **Granular Settings**: Control over app behavior, security, and meter configurations.

---

## 🛠️ Technical Highlights

### Modern Architecture (MVVM)
Fluxion follows the **Model-View-ViewModel (MVVM)** pattern, ensuring a clean separation of concerns.
- **Repository Pattern**: Centralized data management via `FluxionRepository`, abstracting network and local data sources.
- **State Management**: Robust state handling within Composables and ViewModels to ensure a reactive UI.

### Jetpack Navigation 3
The app utilizes the next-generation **Navigation 3** library (`androidx.navigation3`).
- **State-Driven Routing**: Navigation is handled as state, allowing for complex backstack management and seamless transitions between screens like Splash, Login, and the multi-tab Home environment.
- **Nested Navigation**: The Home screen implements a nested navigation graph for the main feature tabs (Dashboard, Analytics, etc.).

### Compose Material Adaptive
Designed for the future, Fluxion's architecture is ready for any form factor.
- **Adaptive Layouts**: Utilizing Material Adaptive components to ensure the UI scales gracefully from compact phones to large-screen tablets and foldables.
- **Edge-to-Edge**: Full implementation of edge-to-edge display, respecting system bars and insets for a truly immersive experience.

### Pure Jetpack Compose UI
- **Custom Canvas**: Real-time charts are implemented using the Compose `Canvas` API for high-performance rendering.
- **Material Design 3**: Strict adherence to M3 guidelines, leveraging the latest components like `NavigationBar`, `Scaffold`, and `TabRow`.

---

## 🎨 Design Style & Aesthetic

### Vibrant Cyan Accent
Fluxion features a **high-energy Cyan color scheme** (`#00E5FF`) that evokes a "tech-forward" and "electric" feel. This primary accent is used consistently across buttons, icons, and progress indicators to create a strong brand identity.

### Sharp & Modern Geometry
Departing from the overly rounded defaults of standard M3, Fluxion employs a **sharp-corner aesthetic** (8dp radius). This choice provides a more industrial, precise, and professional look that aligns perfectly with the "Smart Meter" and "Utility" domain.

### Expressive Motion
- **Pulse Animations**: Status indicators and chart points use subtle animations to signal live connectivity.
- **Smooth Transitions**: Leveraging Navigation 3's capabilities for fluid screen changes and component animations.

---

*Fluxion: Empowering you to take control of your energy, one Watt at a time.*
