# Frontend - Ticket Platform

React frontend for the Smart Urban Mobility Management System (SUMMS).

## Prerequisites
- **Node.js**: 18.x, 20.x, or 22.x LTS ([Download here](https://nodejs.org/en/download))
- **npm**: 9.x or 10.x (comes with Node.js)

Verify installation: `node --version` and `npm --version`

## Quick Start
```bash
# Install dependencies
npm install

# Start development server
npm run dev
```

Open [http://localhost:5173](http://localhost:5173) to view the app.

## Development
- **`npm run dev`**: Development server with hot reload (Vite)
- **`npm run build`**: Build for production
- **`npm run preview`**: Preview production build
- **`npm run lint`**: Check code quality with ESLint

## Project Structure
```
src/
├── components/  # UI components (.js/.jsx + css)
├── pages/       # Page components (.js/.jsx + css)
├── models/      # Data types (.ts)
├── services/    # API calls (.ts)
├── utils/       # Helper functions (.ts)
└── App.tsx      # Main component
```

