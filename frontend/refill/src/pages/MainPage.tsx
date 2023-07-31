import React from 'react';
import '../App.css';
import Navbar from '../components/Navbar'
import Footer from '../components/Footer'
import Counter from '../components/Counter';

// import LoginForm from './LoginForm'
import MemberJoin from '../MemberJoin'
import ButtonTest from '../components/ButtonTest'

const MainApp: React.FC = () => {
  return (
    <div className="MainApp">
      <Navbar />
      <div className="MainApp-body">
        {/* <h1>Hello Refill!</h1>
        <h3>test for redux ! </h3> */}
        {/* <LoginForm /> */}
        <MemberJoin />
        <Counter />
        <ButtonTest/>
        
      </div>
      <Footer />
    </div>
  );
}

export default MainApp;
