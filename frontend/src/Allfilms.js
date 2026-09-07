import React, { useEffect, useState } from 'react';
import Container from '@mui/material/Container';
import Grid from '@mui/material/Grid';
import Card from '@mui/material/Card';
import CardMedia from '@mui/material/CardMedia';
import CardContent from '@mui/material/CardContent';
import Typography from '@mui/material/Typography';
import Rating from '@mui/material/Rating';
import Button from '@mui/material/Button';
import TextField from '@mui/material/TextField';
import { useNavigate } from 'react-router-dom';
import api from './api';

export default function Allfilms() {
  const [films, setFilms] = useState([]);
  const [role, setRole] = useState('NULL');
  const navigate = useNavigate();

  useEffect(() => {
    api.getJson('/films').then(setFilms);
    api.getText('/auth/status').then(setRole);
  }, []);

  const handleSearch = async (e) => {
    const headName = e.target.value;
    const result = await (await api.postJson('/films/search', { headName })).json();
    setFilms(result);
  };

  const addToMyFilms = async (film) => {
    const status = await (await api.postJson('/myfilms/add', { id: film.id })).text();
    alert(status === 'GOOD' ? 'Фильм добавлен в вашу коллекцию' : 'Этот фильм уже есть в вашей коллекции');
  };

  return (
    <Container sx={{ mt: 2 }}>
      <TextField label="Поиск по названию / жанру / режиссёру" fullWidth onChange={handleSearch} sx={{ mb: 2 }} />
      <Grid container spacing={2}>
        {films.map((film) => (
          <Grid item xs={12} sm={6} md={4} key={film.id}>
            <Card>
              <CardMedia component="img" height="250" image={film.urlImage} alt={film.headName} />
              <CardContent>
                <Typography variant="h6">{film.headName}</Typography>
                <Typography variant="body2" color="text.secondary">{film.director} — {film.year}</Typography>
                <Typography variant="body2" color="text.secondary">Жанр: {film.genre}</Typography>
                <Rating value={film.rating} precision={0.5} readOnly />
                <Typography variant="body2">Оценок: {film.marks}</Typography>
                <Button size="small" onClick={() => navigate(`/film/${film.id}`)}>Подробнее и отзывы</Button>
                {role === 'ADMIN' && (
                  <Button size="small" onClick={() => navigate('/editfilm', { state: film })}>Редактировать</Button>
                )}
                {role !== 'NULL' && (
                  <Button size="small" variant="contained" onClick={() => addToMyFilms(film)}>В мою коллекцию</Button>
                )}
              </CardContent>
            </Card>
          </Grid>
        ))}
      </Grid>
    </Container>
  );
}