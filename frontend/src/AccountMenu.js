import React, { useEffect, useState } from 'react';
import AppBar from '@mui/material/AppBar';
import Toolbar from '@mui/material/Toolbar';
import Typography from '@mui/material/Typography';
import Button from '@mui/material/Button';
import Box from '@mui/material/Box';
import api from './api';

export default function AccountMenu() {
  const [role, setRole] = useState('NULL');

  useEffect(() => {
    api.getText('/auth/status').then(setRole);
  }, []);

  return (
    <AppBar position="static">
      <Toolbar>
        <Typography
          variant="h6"
          sx={{ flexGrow: 1, textDecoration: 'none', color: 'inherit' }}
          component="a"
          href="/allfilms"
        >
          FILMBASE
        </Typography>
        <Box>
          <Button color="inherit" href="/allfilms">Фильмы</Button>
          {role !== 'NULL' && <Button color="inherit" href="/myfilms">Мои фильмы</Button>}
          {role !== 'NULL' && <Button color="inherit" href="/searchfriend">Поиск друзей</Button>}
          {role !== 'NULL' && <Button color="inherit" href="/myfriends">Мои друзья</Button>}
          {role !== 'NULL' && <Button color="inherit" href="/addnewfilm">Добавить фильм</Button>}
          {role !== 'NULL' && <Button color="inherit" href="/settingsaccount">Настройки</Button>}
          {role !== 'NULL' && <Button color="inherit" href="/logout">Выход</Button>}
          {role === 'NULL' && <Button color="inherit" href="/signin">Вход</Button>}
          {role === 'NULL' && <Button color="inherit" href="/signup">Регистрация</Button>}
        </Box>
      </Toolbar>
    </AppBar>
  );
}