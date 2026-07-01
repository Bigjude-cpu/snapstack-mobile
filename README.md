# Lensbooks - Photography Portfolio & Business Management App

## Overview

**Lensbooks** is a premium minimalist photography portfolio, feed, and camera specs platform featuring:
- ✨ High-fidelity visual layout
- 🔐 Interactive Authentication module (Email + Google Sign-In)
- 📸 Photography portfolio showcase with EXIF data
- 💼 CRM for managing clients
- 📅 Booking scheduler for shoots
- 🌙 Dark/Light theme support

## Features

### 🔐 Authentication
- Email/Password sign up and login
- Google Sign-In integration
- Persistent user sessions
- Firebase Authentication

### 📸 Feed Screen
- Browse photography portfolio with categories (All, Street, Landscape, Portrait, Architecture)
- View camera specifications (EXIF data)
- Like/unlike photos
- Comment on photos in real-time
- Coil image loading from URL

### 💼 CRM Screen
- Register and manage clients
- Store client contact information (email, phone, company)
- Delete client records
- Real-time Firestore sync

### 📅 Scheduler Screen
- Schedule photo shoots with registered clients
- Select date, time, and package type
- View all bookings
- Cancel bookings
- Real-time Firestore sync

## Tech Stack

### Frontend
- **Kotlin** - Programming language
- **Jetpack Compose** - Modern UI framework
- **Material Design 3** - Design system
- **Coil** - Image loading library

### Backend
- **Firebase Authentication** - User auth
- **Firebase Firestore** - Real-time database
- **Firebase Storage** - Image storage (ready for expansion)
- **Google Generative AI (Gemini)** - AI capabilities (ready for integration)

### Build & Deployment
- **Gradle** - Build system
- **Android Studio** - IDE
- **Min SDK**: 26
- **Target SDK**: 34
- **Java**: 17

## Project Structure

```
app/src/main/java/com/lensbooks/app/
├── MainActivity.kt                 # App entry point
├── auth/
│   ├── AuthViewModel.kt           # Authentication logic
│   └── AuthScreen.kt              # Login/Signup UI
├── ui/
│   ├── screens/
│   │   ├── FeedScreen.kt          # Photography feed
│   │   ├── CrmScreen.kt           # Client management
│   │   └── SchedulerScreen.kt     # Booking scheduler
│   └── theme/
│       └── Theme.kt               # Material 3 theme
└── data/
    ├── models/
    │   └── Models.kt              # Data classes
    └── repository/
        └── DatabaseViewModel.kt    # Firestore logic
```

## Setup Instructions

### Prerequisites
- Android Studio (latest version)
- Android SDK 34
- Gradle 8.5+
- Firebase project

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/Bigjude-cpu/snapstack-mobile.git
   cd snapstack-mobile
   ```

2. **Set up Firebase**
   - Download `google-services.json` from Firebase Console
   - Place it in `app/` directory
   - Update Firebase credentials in `MainActivity.kt` if needed

3. **Open in Android Studio**
   - File → Open → Select project directory
   - Let Gradle sync automatically

4. **Run on Emulator/Device**
   - Press `Shift + F10` or click Run

### Configuration

#### Firebase Setup
1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Create a new project or use existing: `lenbooks-36976`
3. Enable:
   - Authentication (Email/Password + Google)
   - Firestore Database
   - Storage
4. Download `google-services.json` and place in `app/`

#### Google Sign-In
- Client ID: `327424659684-jk7cuvjddilfkc9vag7u0noibkjot4cm.apps.googleusercontent.com`
- Already configured in `AuthScreen.kt`

## API Reference

### AuthViewModel
```kotlin
// Email authentication
fun signInWithEmail(email: String, password: String, onSuccess: () -> Unit)
fun signUpWithEmail(email: String, password: String, fullName: String, onSuccess: () -> Unit)

// Google authentication
fun handleGoogleSignInResult(resultCode: Int, data: Intent, onAuthSuccess: () -> Unit)

// Auth management
fun signOut()
fun setError(message: String)
fun clearError()
```

### DatabaseViewModel
```kotlin
// Client management
fun addClient(name: String, email: String, phone: String, company: String)
fun deleteClient(clientId: String)

// Booking management
fun addBooking(clientName: String, date: String, time: String, packageType: String)
fun deleteBooking(bookingId: String)

// Photo interactions
fun toggleLikePhoto(photoId: String)
fun addCommentToPhoto(photoId: String, commentText: String)
```

## Firebase Collections

### `clients`
```json
{
  "id": "auto-generated",
  "name": "Client Name",
  "email": "client@email.com",
  "phone": "+1234567890",
  "company": "Company Name",
  "userId": "Firebase UID"
}
```

### `bookings`
```json
{
  "id": "auto-generated",
  "clientName": "Client Name",
  "date": "YYYY-MM-DD",
  "time": "HH:MM",
  "packageType": "Portrait Session | Commercial & Brand | Wedding Ceremony | Event & Concert",
  "status": "Confirmed",
  "userId": "Firebase UID"
}
```

## Troubleshooting

### Firebase Not Initializing
- Ensure `google-services.json` is in `app/` directory
- Check Firebase project credentials
- Review logs: `adb logcat | grep Firebase`

### Google Sign-In Not Working
- Verify Web Client ID in `AuthScreen.kt`
- Check SHA-1 fingerprint in Firebase Console
- Generate new SHA-1: `./gradlew signingReport`

### Firestore Data Not Syncing
- Check user is authenticated
- Verify Firestore security rules allow read/write
- Ensure internet connection is active

## Future Enhancements

- [ ] Gemini AI integration for photo descriptions
- [ ] Advanced photo editing tools
- [ ] Payment integration (Stripe/PayPal)
- [ ] Social sharing features
- [ ] Photo upload to Firebase Storage
- [ ] Portfolio templates
- [ ] Client notifications
- [ ] Analytics dashboard

## Contributing

Contributions welcome! Please:
1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

MIT License - feel free to use this project for commercial or personal projects.

## Support

For issues or questions:
- Open an Issue on GitHub
- Check existing documentation
- Review Firebase logs for errors

## Author

**Bigjude-cpu** - [GitHub Profile](https://github.com/Bigjude-cpu)

---

**Last Updated**: July 2026  
**App Version**: 1.0.0  
**Firebase Project**: lenbooks-36976
