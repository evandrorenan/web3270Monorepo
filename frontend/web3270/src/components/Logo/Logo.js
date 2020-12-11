import React from 'react';

import web3270Logo from '../../assets/images/logoWeb3270TransparentBg.png';
import classes from './Logo.css';

const logo = (props) => (
    <div className="Logo">
        <img src={web3270Logo} alt="Web3270" />
    </div>
)

export default logo;