import React, { useEffect, useState } from 'react';
import Container from '@mui/material/Container';
import TextField from '@mui/material/TextField';
import List from '@mui/material/List';
import ListItem from '@mui/material/ListItem';
import ListItemText from '@mui/material/ListItemText';
import Button from '@mui/material/Button';
import api from './api';

export default function Searchfriend() {
  const [users, setUsers] = useState([]);

  useEffect(() => { api.getJson('/friends/candidates').then(setUsers); }, []);

  const handleSearch = async (e) => {
    const login = e.target.value;
    const result = await (await api.postJson('/friends/candidates/search', { login })).json();
    setUsers(result);
  };

  const handleAdd = async (user) => {
    const status = await (await api.postJson('/friends/add', { id: user.id })).text();
    alert(status === 'GOOD' ? 'Пользователь добавлен в друзья' : 'Не удалось добавить (уже в друзьях или это вы сами)');
  };

  return (
    <Container sx={{ mt: 2 }} maxWidth="sm">
      <TextField label="Поиск по логину" fullWidth onChange={handleSearch} sx={{ mb: 2 }} />
      <List>
        {users.map((user) => (
          <ListItem key={user.id} secondaryAction={<Button onClick={() => handleAdd(user)}>Добавить</Button>}>
            <ListItemText primary={user.login} />
          </ListItem>
        ))}
      </List>
    </Container>
  );
}