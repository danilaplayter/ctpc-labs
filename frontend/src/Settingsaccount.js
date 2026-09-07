import React, { useEffect, useState } from 'react';
import Container from '@mui/material/Container';
import FormControlLabel from '@mui/material/FormControlLabel';
import Switch from '@mui/material/Switch';
import Button from '@mui/material/Button';
import Typography from '@mui/material/Typography';
import api from './api';

export default function Settingsaccount() {
  const [user, setUser] = useState(null);

  useEffect(() => { api.getJson('/auth/me').then(setUser); }, []);

  const handleToggle = (field) => (e) => setUser({ ...user, [field]: e.target.checked });

  const handleSave = async () => {
    const message = await (await api.postJson('/auth/me/settings', {
      showForAddFriend: user.showForAddFriend,
      showOtherFilms: user.showOtherFilms,
    })).text();
    alert(message);
  };

  if (!user) return null;

  return (
    <Container sx={{ mt: 2 }} maxWidth="xs">
      <Typography variant="h5" gutterBottom>Настройки приватности</Typography>
      <FormControlLabel
        control={<Switch checked={user.showForAddFriend} onChange={handleToggle('showForAddFriend')} />}
        label="Разрешить добавлять меня в друзья"
      />
      <FormControlLabel
        control={<Switch checked={user.showOtherFilms} onChange={handleToggle('showOtherFilms')} />}
        label="Показывать мою коллекцию друзьям"
      />
      <Button variant="contained" fullWidth sx={{ mt: 2 }} onClick={handleSave}>Сохранить</Button>
    </Container>
  );
}