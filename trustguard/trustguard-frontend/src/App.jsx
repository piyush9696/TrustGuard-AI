import {
    Navigate,
    Route,
    Routes
} from "react-router-dom";

import Login from "./pages/Login";
import Register from "./pages/Register";
import Analyze from "./pages/Analyze";

import ProtectedRoute from "./components/ProtectedRoute";

function App()
{
    return (
        <Routes>

            <Route
                path="/"
                element={
                    <Navigate
                        to="/analyze"
                        replace
                    />
                }
            />

            <Route
                path="/login"
                element={<Login />}
            />

            <Route
                path="/register"
                element={<Register />}
            />

            <Route
                path="/analyze"
                element={
                    <ProtectedRoute>
                        <Analyze />
                    </ProtectedRoute>
                }
            />

            <Route
                path="*"
                element={
                    <Navigate
                        to="/analyze"
                        replace
                    />
                }
            />

        </Routes>
    );
}

export default App;