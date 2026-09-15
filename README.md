# Zenzeleni

A community-driven loyalty and rewards mobile app built for the South African market, developed as part of the PROG6212/APPR6312 Portfolio of Evidence.

## About

Zenzeleni ("do it yourselves" / "do it together" in isiZulu) lets users complete challenges, earn points, and redeem rewards from partner merchants — combining gamified engagement, offline-friendly design, and multi-language support to serve South African users across all levels of digital literacy.

## Features Implemented (Part 2 Prototype)

- User registration and login with encrypted password storage (via Firebase Authentication)
- Settings screen: change password, log out, language selection (English / isiZulu)
- Live connection to a cloud database (Cloud Firestore) displaying real challenge data
- Unit tests covering validation and points logic
- Automated build and test pipeline via GitHub Actions

## A Note on Backend Choice

The original Planning & Design Document specified a custom PHP REST API with a MySQL database. During development, the backend was switched to Firebase (Authentication + Cloud Firestore) to deliver the required features — encrypted authentication, a live database connection, and (in the final PoE) real-time notifications — more reliably within the project timeline. This satisfies the brief's requirement to connect to "a REST API you create, or any storage mechanism that fits the idea you have, that exists on the internet."

## Tech Stack

- Kotlin, Android (min SDK 24)
- Firebase Authentication
- Cloud Firestore
- GitHub Actions for CI

## Demo Video

[Video link to be added]

## AI Use Disclosure

[To be completed — describe any AI tools used and how, max 500 words]