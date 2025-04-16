export type Task = {
      taskId: string,
      userId: string,
      title: string,
      summary:string,
      dueDate: string,
      docName?:string
  }

  export type NewTask = {
      title: string,
      summary:string,
      dueDate: string
      taskDoc:File
  }