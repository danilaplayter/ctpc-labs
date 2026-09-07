import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import Container from '@mui/material/Container';
import Typography from '@mui/material/Typography';
import TextField from '@mui/material/TextField';
import Button from '@mui/material/Button';
import List from '@mui/material/List';
import ListItem from '@mui/material/ListItem';
import ListItemText from '@mui/material/ListItemText';
import Box from '@mui/material/Box';
import api from './api';

export default function FilmDetails() {
  const { id } = useParams();
  const [reviews, setReviews] = useState([]);
  const [text, setText] = useState('');
  const [me, setMe] = useState(null);

  const loadReviews = () => api.getJson(`/reviews/film/${id}`).then(setReviews);

  useEffect(() => {
    loadReviews();
    api.getJson('/auth/me').then(setMe).catch(() => setMe(null));
  }, [id]);

  const handleAdd = async (e) => {
    e.preventDefault();
    if (!text.trim()) return;
    await api.postJson('/reviews/add', { cinemaId: Number(id), text });
    setText('');
    loadReviews();
  };

  const handleDelete = async (reviewId) => {
    await api.postJson('/reviews/delete', { reviewId });
    loadReviews();
  };

  return (
    <Container sx={{ mt: 2 }} maxWidth="sm">
      <Typography variant="h5" gutterBottom>Отзывы к фильму</Typography>

      {me && (
        <Box component="form" onSubmit={handleAdd} sx={{ mb: 3 }}>
          <TextField
            fullWidth
            multiline
            minRows={2}
            label="Ваш отзыв"
            value={text}
            onChange={(e) => setText(e.target.value)}
          />
          <Button type="submit" variant="contained" sx={{ mt: 1 }}>Отправить</Button>
        </Box>
      )}

      <List>
        {reviews.map((review) => (
          <ListItem
            key={review.id}
            secondaryAction={
              me && me.id === review.user.id && (
                <Button color="error" size="small" onClick={() => handleDelete(review.id)}>Удалить</Button>
              )
            }
          >
            <ListItemText primary={review.user.login} secondary={review.text} />
          </ListItem>
        ))}
        {reviews.length === 0 && <Typography color="text.secondary">Отзывов пока нет</Typography>}
      </List>
    </Container>
  );
}