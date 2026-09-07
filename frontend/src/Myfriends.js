import React, { useEffect, useState } from 'react';
import Container from '@mui/material/Container';
import List from '@mui/material/List';
import ListItem from '@mui/material/ListItem';
import ListItemText from '@mui/material/ListItemText';
import Button from '@mui/material/Button';
import { useNavigate } from 'react-router-dom';
import api from './api';

export default function Myfriends() {
  const [friends, setFriends] = useState([]);
  const navigate = useNavigate();

  const load = () => api.getJson('/friends').then(setFriends);

  useEffect(() => { load(); }, []);

  const handleRemove = async (friend) => {
    await api.postJson('/friends/remove', { id: friend.userTwo.id });
    load();
  };

  return (
    <Container sx={{ mt: 2 }} maxWidth="sm">
      <List>
        {friends.map((fr) => (
          <ListItem
            key={fr.id}
            secondaryAction={
              <>
                <Button onClick={() => navigate('/showusercollection', { state: fr.userTwo })}>Коллекция</Button>
                <Button color="error" onClick={() => handleRemove(fr)}>Удалить</Button>
              </>
            }
          >
            <ListItemText primary={fr.userTwo.login} />
          </ListItem>
        ))}
        {friends.length === 0 && <ListItemText primary="Пока нет друзей" />}
      </List>
    </Container>
  );
}