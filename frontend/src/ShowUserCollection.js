import React, { useEffect, useState } from 'react';
import { useLocation } from 'react-router-dom';
import Container from '@mui/material/Container';
import Grid from '@mui/material/Grid';
import Card from '@mui/material/Card';
import CardMedia from '@mui/material/CardMedia';
import CardContent from '@mui/material/CardContent';
import Typography from '@mui/material/Typography';
import Rating from '@mui/material/Rating';
import api from './api';

export default function ShowUserCollection() {
  const { state: friend } = useLocation();
  const [items, setItems] = useState([]);

  useEffect(() => {
    if (friend) api.postJson('/friends/collection', { id: friend.id }).then((r) => r.json()).then(setItems);
  }, [friend]);

  if (!friend) return <Typography sx={{ mt: 4 }}>Друг не выбран</Typography>;

  return (
    <Container sx={{ mt: 2 }}>
      <Typography variant="h5" gutterBottom>Коллекция пользователя {friend.login}</Typography>
      <Grid container spacing={2}>
        {items.map((cu) => (
          <Grid item xs={12} sm={6} md={4} key={cu.id}>
            <Card>
              <CardMedia component="img" height="250" image={cu.cinema.urlImage} alt={cu.cinema.headName} />
              <CardContent>
                <Typography variant="h6">{cu.cinema.headName}</Typography>
                <Typography variant="body2">Статус: {cu.statusCinema}</Typography>
                <Rating value={cu.ratingUser} readOnly />
              </CardContent>
            </Card>
          </Grid>
        ))}
      </Grid>
    </Container>
  );
}