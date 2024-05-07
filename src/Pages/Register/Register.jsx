import React, { useState } from 'react';
import './Register.css';
import { useLocation } from 'react-router-dom';

const Register = () => {
  const location = useLocation();
  const isRegisterPage = location.pathname === '/register';
  const [step, setStep] = useState(1);

  const nextStep = () => {
    setStep(step + 1);
  };

  const prevStep = () => {
    setStep(step - 1);
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    if (step === 1) {
      nextStep(); // Move to the next step if it's the first step
    } else {
      // Step 2, registration is complete, redirect to login page
      window.location.href = '/login';
    }
  };

  return (
    <>
      {isRegisterPage && (
        <div className={`container ${isRegisterPage ? 'login-background' : ''}`}>
          <div className="wrapper">
            <form onSubmit={handleSubmit}>
              {step === 1 && (
                <div id="step1">
                  <h2>Step 1: Account Information</h2>
                  <div className="input-box">
                    <label htmlFor="email">Email:</label>
                    <input type="email" id="email" name="email" required />
                  </div>

                  <div className="input-box">
                    <label htmlFor="username">Username:</label>
                    <input type="text" id="username" name="username" required />
                  </div>

                  <div className="input-box">
                    <label htmlFor="password">Password:</label>
                    <input type="password" id="password" name="password" required />
                  </div>

                  <button type="submit">Next</button>
                </div>
              )}

              {step === 2 && (
                <div id="step2">
                  <h2>Step 2: Personal Information</h2>
                  <div className="input-box">
                    <label htmlFor="fullname">Full Name:</label>
                    <input type="text" id="fullname" name="fullname" required />
                  </div>

                  <div className="input-box">
                    <label htmlFor="dob">Date of Birth:</label>
                    <input type="date" id="dob" name="dob" required />
                  </div>
                
                  <button type="button" onClick={prevStep}>Back</button>
                  <button type="submit">Complete</button>
                </div>
              )}
            </form>
          </div>
        </div>
      )}
    </>
  );
};

export default Register;
