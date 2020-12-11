import React, { Component } from 'react';

import Toolbar from './Toolbar';
import Content from './Content';
import './Layout.css';
import NavigationBar from './NavigationBar';

class Layout extends Component {

    constructor() {
        super();
        this.state = { 
            items:[{ id:"SessionItem", name:"Session", isActive: true } ,
                   { id:"Report", name:"Abend Report", isActive: false },
                   { id:"RequestReport", name:"Request new abend Report", isActive: false }]};
    }

    onclick = (event) => {
        const localstate = Object.assign({}, this.state);

        for (let index = 0; index < localstate.items.length; index++) {
            if (localstate.items[index].id === event.target.id) {
                localstate.items[index].isActive = true;
            } else {
                localstate.items[index].isActive = false;
            }
        }
        this.setState(localstate);
    }

    activeTab = () => {
        for (let index = 0; index < this.state.items.length; index++) {
            if (this.state.items[index].isActive === true) {
                return this.state.items[index].id;
            }
        }
        return "";
    }

    render() {
        return (
            <div className="Layout">
                <Toolbar />
                <NavigationBar state={this.state} onclick={this.onclick} />
                <Content activeTab={this.activeTab} />
            </div>
        );
    }
}

export default Layout;