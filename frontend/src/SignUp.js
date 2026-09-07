import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Container from '@mui/material/Container';
import TextField from '@mui/material/TextField';
import Button from '@mui/material/Button';
import Typography from '@mui/material/Typography';
import Box from '@mui/material/Box';
import api from './api';

export default function SignUp() {
  const navigate = useNavigate();
  const [form, setForm] = useState({ login: '', email: '', password: '' });
  const [message, setMessage] = useState('');

  const handleChange = (field) => (e) => setForm({ ...form, [field]: e.target.value });

  const handleSubmit = async (e) => {
    e.preventDefault();
    const result = await (await api.postJson('/auth/register', form)).text();
    if (result === 'GOOD') {
      navigate('/signin');
    } else if (result === 'ERRORLOGIN') {
      setMessage('Такой логин уже занят');
    } else if (result === 'ERROREMAIL') {
      setMessage('Такой email уже используется');
    }
  };

  return (
    <Container maxWidth="xs">
      <Box component="form" onSubmit={handleSubmit} sx={{ mt: 8 }}>
        <Typography variant="h5">Регистрация</Typography>
        <TextField fullWidth margin="normal" label="Логин" value={form.login} onChange={handleChange('login')} />
        <TextField fullWidth margin="normal" label="Email" value={form.email} onChange={handleChange('email')} />
        <TextField fullWidth margin="normal" label="Пароль" type="password" value={form.password} onChange={handleChange('password')} />
        {message && <Typography color="error">{message}</Typography>}
        <Button type="submit" fullWidth variant="contained" sx={{ mt: 2 }}>Зарегистрироваться</Button>
      </Box>
    </Container>
  );
}