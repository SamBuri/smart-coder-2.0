export default interface HttpStrategy {
  resultHandler?: (response: any) => any;
  errorHandler?: (error: any) => any;
}
