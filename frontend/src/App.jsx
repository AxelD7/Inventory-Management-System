import { useState } from "react";

import Home from "./pages/Home";
import Login from "./pages/Login";

import {Routes, Route } from 'react-router-dom'

function App() {
  return (
    <div className="main-content w-full min-h-screen flex flex-col items-center justify-center bg-slate-100">
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/login" element={<Login />} />
      </Routes>
    </div>
  );
}

export default App;
