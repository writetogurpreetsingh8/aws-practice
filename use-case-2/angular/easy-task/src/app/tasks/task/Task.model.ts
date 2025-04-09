export type Task = {
      taskId: string,
      userId: string,
      title: string,
      summary:string,
      dueDate: string
  }

  export type NewTask = {
      title: string,
      summary:string,
      dueDate: string
  }