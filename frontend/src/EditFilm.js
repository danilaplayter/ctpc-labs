import React, { useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import Container from '@mui/material/Container';
import TextField from '@mui/material/TextField';
import Button from '@mui/material/Button';
import Typography from '@mui/material/Typography';
import Box from '@mui/material/Box';
import api from './api';

export default function EditFilm() {
  const { state } = useLocation();
  const navigate = useNavigate();
  const [form, setForm] = useState(state || {});

  const handleChange = (field) => (e) => setForm({ ...form, [field]: e.target.value });

  const handleSubmit = async (e) => {
    e.preventDefault();
    const message = await (await api.postJson('/films/edit', form)).text();
    alert(message);
    navigate('/allfilms');
  };

  if (!state) {
    return <Typography sx={{ mt: 4 }}>Фильм не выбран</Typography>;
  }

  return (
    <Container maxWidth="sm">
      <Box component="form" onSubmit={handleSubmit} sx={{ mt: 4 }}>
        <Typography variant="h5" gutterBottom>Редактирование фильма</Typography>
        <TextField fullWidth margin="normal" label="Название" value={form.headName} onChange={handleChange('headName')} />
        <TextField fullWidth margin="normal" label="Постер (URL)" value={form.urlImage} onChange={handleChange('urlImage')} />
        <TextField fullWidth margin="normal" label="Режиссёр" value={form.director} onChange={handleChange('director')} />
        <TextField fullWidth margin="normal" label="Жанр" value={form.genre} onChange={handleChange('genre')} />
        <TextField fullWidth margin="normal" label="Год" value={form.year} onChange={handleChange('year')} />
        <TextField fullWidth margin="normal" label="Описание" multiline minRows={3} value={form.about} onChange={handleChange('about')} />
        <Button type="submit" variant="contained" sx={{ mt: 2 }}>Сохранить</Button>
      </Box>
    </Container>
  );
}