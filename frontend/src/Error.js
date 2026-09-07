import React from 'react';
import Container from '@mui/material/Container';
import Typography from '@mui/material/Typography';
import Button from '@mui/material/Button';

export default function Error() {
  return (
    <Container sx={{ mt: 4 }}>
      <Typography variant="h5">Страница не найдена</Typography>
      <Button href="/allfilms">На главную</Button>
    </Container>
  );
}