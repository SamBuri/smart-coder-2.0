const navUtils = {

    editRoute(object, component, auth = true) {
        return {
            path: `/${object}/:mode`,
            name: object,
            component: component,
            meta: { auth: auth },
        }
    },

    viewRoute(object, component, auth = true) {
        return {
            path: `/${object}`,
            name: object,
            component: component,
            meta: { auth: auth },
        }
    },

    allRoutes(object,  editComponent, path,viewComponent, auth = true) {

        return [
            this.editRoute(object, editComponent, auth),
            this.viewRoute(path, viewComponent, auth),
        ];

    },


    allRoles(object) {
        return [`${object}_all`, `${object}_create`, `${object}_update`, `${object}_view`, `${object}_delete`];
    },

    createRoles(object) {
        return [`${object}_all`, `${object}_create`];
    },
    updateRoles(object) {
        return [`${object}_all`, `${object}_update`];
    },
    deleteRoles(object) {
        return [`${object}_all`, `${object}_delete`];
    },
    viewRoles(object) {
        return [`${object}_all`, `${object}_view`];
    },

    viewChild(module, object, route, icon = "mdi-table") {
        return {
            id: `${module}.${object}.view`,
            title: "View", icon: icon, to: { name: route, },
            requires: this.viewRoles(object)
        }
    },
    newChild(module, object, route, icon = "mdi-plus-circle") {
        return {
            id: `${module}.${object}.new`,
            title: "New", icon: icon, to: { name: object, params: { mode: 0 } },
            requires: this.createRoles(object)
        }
    },

    editChild(module, object, route, icon = "mdi-pencil") {
        return {
            id: `${module}.${object}.edit`,
            title: "Edit", icon: icon, to: { name: object, params: { mode: 1 } },
            requires: this.updateRoles(object)
        }
    },

     prieviewChild(module, object, route, icon = "mdi-history") {
        return {
            id: `${module}.${object}.preview`,
            title: "Preview", icon: icon, to: { name: object, params: { mode: 2 } },
            requires: this.viewRoles(object)
        }
    },

    historyChild(module, object, route, icon = "mdi-history") {
        return {
            id: `${module}.${object}.history`,
            title: "History", icon: icon, to: { name: object, params: { mode: 3 } },
            requires: this.viewRoles(object)
        }
    },

    allChildren(module, object, route, addHistory = true) {
        return [
            this.viewChild(module, object, route),
            this.newChild(module, object, route),
            this.editChild(module, object, route)
        ].concat(addHistory ? [this.historyChild(module, object, route)] : []);

    },
      createViewChildren(module, object, route, addHistory = true) {
        return [
            this.viewChild(module, object, route),
            this.newChild(module, object, route),
            this.prieviewChild(module, object, route)
        ].concat(addHistory ? [this.historyChild(module, object, route)] : []);

    }
}

export default navUtils;
// This utility module provides functions to generate role-based access control (RBAC) roles and navigation children for different objects in the application.
// It includes methods to create roles for all, create, update, delete, and view actions, as well as methods to generate navigation children for viewing, creating, editing, and viewing history of objects.
// The `navUtils` object contains methods that return arrays of roles or navigation children based on the provided object and module names.
// The utility is designed to be used in a Vue.js application, particularly with Vue Router for navigation and Pinia for state management.
// It helps in maintaining a consistent structure for role management and navigation across different modules of the application.