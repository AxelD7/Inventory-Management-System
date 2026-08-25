function Login() {
  return (
    <div className="login border-2 max-w-md rounded-xl bg-white border-slate-200 p-6 shadow-sm flex-col">
      <div className="login-title mb-5">
        <h2 className="font-bold text-2xl text-center mb-3">
          Asset Management System
        </h2>
        <hr />
      </div>
      <form className="login-form gap-8 items-center">
        <label className="text-xl block">Username:</label>
        <input
          className="username w-full mb-2 border-2 rounded-md border-slate-600 bg-gray-200"
          placeholder="Username"
        ></input>
        <label className="text-xl block">Password:</label>
        <input
          className="password w-full mb-2 border-2 rounded-md border-slate-600 bg-gray-200"
          placeholder="Password"
        ></input>
        <button
          type="submit"
          className="bg-blue-600 w-full border-2 rounded-2xl border-gray-500"
        >
          Log in
        </button>
      </form>
    </div>
  );
}

export default Login;
