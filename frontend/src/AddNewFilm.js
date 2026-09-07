import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Container from '@mui/material/Container';
import TextField from '@mui/material/TextField';
import Button from '@mui/material/Button';
import Typography from '@mui/material/Typography';
import Box from '@mui/material/Box';
import api from './api';

export default function AddNewFilm() {
  const navigate = useNavigate();
  const [form, setForm] = useState({ headName: '', urlImage: '', director: '', genre: '', year: '', about: '' });

  const handleChange = (field) => (e) => setForm({ ...form, [field]: e.target.value });

  const handleSubmit = async (e) => {
    e.preventDefault();
    const payload = { ...form, year: Number(form.year) };
    const message = await (await api.postJson('/films/add', payload)).text();
    alert(message);
    navigate('/allfilms');
  };

  return (
    <Container maxWidth="sm">
      <Box component="form" onSubmit={handleSubmit} sx={{ mt: 4 }}>
        <Typography variant="h5" gutterBottom>Добавление фильма</Typography>
        <TextField fullWidth margin="normal" label="Название" value={form.headName} onChange={handleChange('headName')} required />
        <TextField fullWidth margin="normal" label="Постер (URL)" value={form.urlImage} onChange={handleChange('urlImage')} required />
        <TextField fullWidth margin="normal" label="Режиссёр" value={form.director} onChange={handleChange('director')} required />
        <TextField fullWidth margin="normal" label="Жанр" value={form.genre} onChange={handleChange('genre')} required />
        <TextField fullWidth margin="normal" label="Год" value={form.year} onChange={handleChange('year')} required />
        <TextField fullWidth margin="normal" label="Описание" multiline minRows={3} value={form.about} onChange={handleChange('about')} required />
        <Button type="submit" variant="contained" sx={{ mt: 2 }}>Добавить</Button>
      </Box>
    </Container>
  );
}