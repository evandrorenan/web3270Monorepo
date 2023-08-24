import React, { Component } from 'react';

import Toolbar from './toolbar/Toolbar';
import Content from './mainContent/Content';
import './Layout.css';
import NavigationBar from './navigationBar/NavigationBar';

class Layout extends Component {

    constructor() {
        super();
        this.state = { 
            items:[{ id:"SessionItem", name:"Session", isActive: false } ,
                   { id:"Report", name:"Abend Report", isActive: false },
                   { id:"RequestSysoutEvt", name:"Sysout download", isActive: false },
                   { id:"ManualUploadForm", name:"Upload manual", isActive: true }]};
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
                <div className="MainDiv">
                    <NavigationBar state={this.state} onclick={this.onclick} />
                    <Content activeTab={this.activeTab} />
                </div>
            </div>
        );
    }
}

export default Layout;