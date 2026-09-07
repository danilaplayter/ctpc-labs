import React from 'react';
import { Outlet } from 'react-router-dom';
import AccountMenu from './AccountMenu';

export default function Mainer() {
  return (
    <>
      <AccountMenu />
      <Outlet />
    </>
  );
}