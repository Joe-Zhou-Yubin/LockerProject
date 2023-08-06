import { render } from 'react-dom';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import 'bootstrap/dist/css/bootstrap.min.css';
import Locker from './Locker';
import LockerAdmin from './LockerAdmin';
import LockerDisplay from './LockerDisplay';
import LockerCreate from './LockerCreate';
import LockerUpdate from './LockerUpdate';
import React, { useEffect, useState } from 'react';


// Add the main App component that includes the route guarding
function App() {

  return (
    <Router>
      <Routes>
        <Route path="/*" element={<Locker />} />
        <Route path="/lockercreate" element={<LockerCreate />} />
        <Route path="/lockeradmin" element={<LockerAdmin />} />
        <Route path="/lockerdisplay/:lockerId" element={<LockerDisplay />} />
        <Route path="/lockerupdate/:lockerId" element={<LockerUpdate />} />
      </Routes>
    </Router>
  );
}

render(<App />, document.getElementById('root'));
