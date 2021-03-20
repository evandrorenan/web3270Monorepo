import React, { Component } from 'react';

import NavigationBarItem from './NavigationBarItem';

class NavigationBar extends Component {

    constructor(props) {
        super();
        this.state = props.state;
    }

    render() {
        return (
            <div className="NavigationBar" key="NavigationBar">
                {this.state.items.map((item) => {
                    return <NavigationBarItem key={item.id} 
                                              id={item.id} 
                                              name={item.name} 
                                              isActive={item.isActive} 
                                              onclick={this.props.onclick} />
                })}
            </div>
        );
    }
}

export default NavigationBar;