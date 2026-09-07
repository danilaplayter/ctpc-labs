import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Container from '@mui/material/Container';
import TextField from '@mui/material/TextField';
import Button from '@mui/material/Button';
import Typography from '@mui/material/Typography';
import Box from '@mui/material/Box';
import api from './api';

export default function SignIn() {
  const navigate = useNavigate();
  const [login, setLogin] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    const status = await (await api.postJson('/auth/login', { login, password })).text();
    if (status === 'AUTH') {
      navigate('/allfilms');
    } else {
      setError('Неверный логин или пароль');
    }
  };

  return (
    <Container maxWidth="xs">
      <Box component="form" onSubmit={handleSubmit} sx={{ mt: 8 }}>
        <Typography variant="h5">Вход</Typography>
        <TextField fullWidth margin="normal" label="Логин" value={login} onChange={(e) => setLogin(e.target.value)} />
        <TextField fullWidth margin="normal" label="Пароль" type="password" value={password} onChange={(e) => setPassword(e.target.value)} />
        {error && <Typography color="error">{error}</Typography>}
        <Button type="submit" fullWidth variant="contained" sx={{ mt: 2 }}>Войти</Button>
      </Box>
    </Container>
  );
}