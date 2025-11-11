# **Swapi**

**A private digital marketplace for university communities.**

Swapi is a digital platform designed for university communities to buy, sell, rent, and offer services in a secure and private environment. The initial deployment is planned for **La Salle University in Chihuahua, Mexico**, but the system is built with scalability in mind.

---

## **About the Project**

Swapi functions as a **private marketplace** exclusive to students and faculty members. Its goal is to create a trusted community where users can interact through verified university accounts.

Users will be able to:

* Post listings for products, rentals, and services.  
* Search and filter through categories.  
* Manage their own listings and profiles.

The project includes both a **mobile application (Android)** and a **web platform**, which share a single backend and database.

---

## **Architecture**

Swapi follows a **client-server architecture** divided into three main components. Both clients (web and mobile) communicate with the same backend API via **GraphQL**.

swapi-project/  
├─ backend/       → Node.js \+ Apollo Server (GraphQL) \+ MongoDB  
├─ web-frontend/  → React (in development)  
└─ mobile-app/    → Android (Jetpack Compose \+ MVVM, separate repo)

---

## **Tech Stack**

The development environment consists of:

### **Backend**

* Node.js (v18+)  
* Apollo Server (GraphQL)  
* MongoDB (via Mongoose)  
* JWT Authentication  
* Express.js  
* Docker (optional for local MongoDB setup)

### **Frontend (Web)**

* React.js or Next.js (TBD)  
* Apollo Client for GraphQL  
* Deployment on Netlify or Vercel

### **Frontend (Mobile)**

* Android Studio  
* Kotlin \+ Jetpack Compose  
* MVVM Architecture  
* Consumes the same GraphQL API

---

## **Getting Started (Backend)**

Follow these steps to get the backend development environment running:

1. Navigate to the backend directory:  
   Bash  
   cd backend

2. Install dependencies:  
   Bash  
   npm install

3. Create a .env file based on .env.example and fill in your credentials:  
   Fragmento de código  
   MONGODB\_URI=your\_mongo\_uri\_here  
   JWT\_SECRET=your\_secret\_key

4. Start the development server:  
   Bash  
   npm run dev

5. You can access the GraphQL playground at:  
   http://localhost:4000/graphql

---

## **Future Goals**

* Implement user-to-user messaging.  
* Add file/image uploading for product listings.  
* Expand to more universities.  
* Integrate GraphQL subscriptions for real-time updates.  
* Deploy the backend to Render and the web frontend to Netlify.

---

## **License**

This project is being developed for educational purposes as part of a university software engineering course.  
All rights reserved to the Swapi development team.