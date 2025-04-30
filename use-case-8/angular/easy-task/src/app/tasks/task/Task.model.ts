export type Task = {
      taskId: string,
      userId: string,
      title: string,
      summary:string,
      dueDate: string,
      docName?:string,
      schedule:boolean
  }

  export type NewTask = {
      title: string,
      summary:string,
      dueDate: string
      taskDoc:File,
      schedule:boolean
  }

  export type Response = {
    scheduleTask:number,
    reponseBody:[]
  }