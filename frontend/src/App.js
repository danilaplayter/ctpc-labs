import React from 'react';
import { Routes, Route } from 'react-router-dom';
import Mainer from './Mainer';
import Mainpage from './Mainpage';
import SignIn from './SignIn';
import SignUp from './SignUp';
import Allfilms from './Allfilms';
import FilmDetails from './FilmDetails';
import Myfilms from './Myfilms';
import AddNewFilm from './AddNewFilm';
import EditFilm from './EditFilm';
import Searchfriend from './Searchfriend';
import Myfriends from './Myfriends';
import ShowUserCollection from './ShowUserCollection';
import Settingsaccount from './Settingsaccount';
import Logout from './Logout';
import Error from './Error';

export default function App() {
  return (
    <Routes>
      <Route path="/" element={<Mainer />}>
        <Route index element={<Mainpage />} />
        <Route path="signin" element={<SignIn />} />
        <Route path="signup" element={<SignUp />} />
        <Route path="allfilms" element={<Allfilms />} />
        <Route path="film/:id" element={<FilmDetails />} />
        <Route path="myfilms" element={<Myfilms />} />
        <Route path="addnewfilm" element={<AddNewFilm />} />
        <Route path="editfilm" element={<EditFilm />} />
        <Route path="searchfriend" element={<Searchfriend />} />
        <Route path="myfriends" element={<Myfriends />} />
        <Route path="showusercollection" element={<ShowUserCollection />} />
        <Route path="settingsaccount" element={<Settingsaccount />} />
        <Route path="logout" element={<Logout />} />
        <Route path="*" element={<Error />} />
      </Route>
    </Routes>
  );
}