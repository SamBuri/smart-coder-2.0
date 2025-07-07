// src/root/RootStore.ts

import { defineStore } from 'pinia';
import httpMethods from './http'
import type HttpStrategy from './HttpStrategy'; // if using strategy types

interface Results<T = any> {
    success: boolean;
    message: string;
    show: boolean;
    entity: T;
}

interface Request<T = any> {
    path: string;
    body: T;
    httpStrategy?: HttpStrategy;
}

export const defineRootStore = defineStore('root', {
    state: () => ({
        data: {} as Record<string, any>,
        centralMessage: '' as string,
        loading: false as boolean,
        passedInsurance: null as any,
        dataLoading: false as boolean,
        path: '' as string,
        obj: null as any,
        objLoading: false as boolean,
        results: null as Results | null,
        deleteLoading: false as boolean,
        mode: 0 as number,
    }),

    actions: {

        setResults(data: any) {
            if (data !== null)
                this.results = {
                    success: data.success,
                    message: data.message,
                    show: data.show,
                    entity: data.entity,
                };
            else this.results = null;
        },

        strategyResults(response: any, httpStrategy?: HttpStrategy, show = true) {
            console.log('Response', response);
            const resultHandler = httpStrategy?.resultHandler;
            const result = resultHandler ? resultHandler(response) : response.data;
            result.show = show;
            this.results = result;
            return this.results;
        },

        strategyError(error: any, httpStrategy?: HttpStrategy) {
            console.error('Error occurred!', error);
            const errorHandler = httpStrategy?.errorHandler;
            const result = errorHandler
                ? errorHandler(error)
                : {
                    success: false,
                    message: 'Unknown Error occurred. Please try again later',
                    show: true,
                    entity: null,
                };
            this.results = result;
            return result;
        },

        async post(request: Request, show = true) {
            this.results = null;
            console.log('Request', request);
            this.loading = true;

            try {
                const response = await httpMethods.post(request.path, request.body);
                return this.strategyResults(response, request.httpStrategy, show);
            } catch (error: any) {
                return this.strategyError(error, request.httpStrategy);
            } finally {
                this.loading = false;
            }
        },

        async put(request: Request) {
            this.results = null;
            this.loading = true;
            setTimeout(() => { }, 2000);
            console.log("Request", request);
            let updateResults = await httpMethods
                .put(request.path, request.body)
                .then((response) => {

                    console.log("Response", response.data);

                    return this.strategyResults(response, request.httpStrategy);

                })
                .catch((error) => {
                    return this.strategyError(error, request.httpStrategy);
                })
                .finally(() => (this.loading = false));
            return updateResults;
        },

        async get(url: string) {
            if (!url) return null;
            this.objLoading = true;
            this.obj = null;
            let data = await httpMethods
                .get(url)
                .then((response) => {
                    this.obj = response.data;
                    console.log("Returned Data", this.obj);
                    if (!this.obj) {
                        this.results = {
                            success: false,
                            message: "No data Found",
                            show: true,
                        } as Results;
                    }
                    return this.obj;
                })
                .catch((e) => {
                    console.log("An error occured " + e);
                    this.results = {
                        success: false,
                        message: "An error occured",
                        show: true,
                    } as Results;
                    return null;
                })
                .finally(() => {
                    this.objLoading = false;
                });
            return data;
        },

        getData(path: string) {
            this.results = null;

            this.dataLoading = true;
            httpMethods
                .get(path)
                .then((response) => {
                    this.data = response.data;
                    console.log("Response", response);
                    this.results = { success: true, message: "Successful", show: false } as Results;
                })
                .catch((error) => {
                    console.log(error);
                    this.data = [];

                    this.results = { success: false, message: error, show: true } as Results;
                })
                .finally(() => this.dataLoading = false);
        },
        async fetch(endpoint: string, pre: Function, success: Function, end: Function) {
            if (pre) pre();

            let res = await httpMethods
                .get(endpoint)
                .then((res) => {
                   console.log(res);
                    success(res)
                    return res.data;
                })
                .catch((error) => {
                    console.log("Error loading data", error);
                    this.results = {
                        success: false,
                        message: "Error loading data",
                        show: true,
                    } as Results;

                    return [];
                })
                .finally(() => end());
            return res;
        },

        async doPost(endpoint: string, payload: any, pre: any, success: any, end: any) {
            if (pre) pre();

            let res = await httpMethods
                .post(endpoint, payload)
                .then((res) => {

                    success(res)
                    return res.data;
                })
                .catch((error) => {
                    console.log("Error Posting data", error);
                    this.results = {
                        success: false,
                        message: "Error Posting data",
                        show: true,
                    } as Results;

                    return [];
                })
                .finally(() => end());
            return res;
        },

    },
});
