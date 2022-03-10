package com.company.client.ClientNetwork.States;

import com.company.client.ClientNetwork.Client;

public interface State {

    boolean proceed();
    void setContext(Client context);

}
