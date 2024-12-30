package com.hfad2.projectmanagmentapplication.models;

public enum EmployeeStatus {
    AVAILABLE,      // Employee can take on new tasks
    ON_LEAVE,       // Employee is on vacation or other leave
    BUSY,          // Employee has maximum workload
    INACTIVE       // Employee is not currently active in the system
}