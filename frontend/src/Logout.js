import React, { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import api from './api';

export default function Logout() {
  const navigate = useNavigate();
  useEffect(() => {
    api.getText('/auth/logout').then(() => navigate('/'));
  }, [navigate]);
  return null;
}