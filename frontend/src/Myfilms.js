import React, { useEffect, useState } from 'react';
import Container from '@mui/material/Container';
import Grid from '@mui/material/Grid';
import Card from '@mui/material/Card';
import CardMedia from '@mui/material/CardMedia';
import CardContent from '@mui/material/CardContent';
import Typography from '@mui/material/Typography';
import Rating from '@mui/material/Rating';
import Select from '@mui/material/Select';
import MenuItem from '@mui/material/MenuItem';
import Button from '@mui/material/Button';
import api from './api';

const STATUSES = ['Без статуса просмотра', 'Смотрю', 'Просмотрено', 'Хочу посмотреть'];

export default function Myfilms() {
  const [items, setItems] = useState([]);

  const load = () => api.getJson('/myfilms').then(setItems);

  useEffect(() => { load(); }, []);

  const handleRate = async (cu, rating) => {
    await api.postJson('/myfilms/rate', { id: cu.id, ratingUser: rating, statusCinema: cu.statusCinema });
    load();
  };

  const handleStatus = async (cu, status) => {
    await api.postJson('/myfilms/rate', { id: cu.id, ratingUser: cu.ratingUser, statusCinema: status });
    load();
  };

  const handleRemove = async (cu) => {
    await api.postJson('/myfilms/remove', { id: cu.cinema.id });
    load();
  };

  return (
    <Container sx={{ mt: 2 }}>
      <Grid container spacing={2}>
        {items.map((cu) => (
          <Grid item xs={12} sm={6} md={4} key={cu.id}>
            <Card>
              <CardMedia component="img" height="250" image={cu.cinema.urlImage} alt={cu.cinema.headName} />
              <CardContent>
                <Typography variant="h6">{cu.cinema.headName}</Typography>
                <Select fullWidth size="small" value={cu.statusCinema} onChange={(e) => handleStatus(cu, e.target.value)}>
                  {STATUSES.map((s) => <MenuItem key={s} value={s}>{s}</MenuItem>)}
                </Select>
                <Rating value={cu.ratingUser} onChange={(e, v) => handleRate(cu, v || 0)} />
                <Button color="error" size="small" onClick={() => handleRemove(cu)}>Удалить из коллекции</Button>
              </CardContent>
            </Card>
          </Grid>
        ))}
      </Grid>
    </Container>
  );
}