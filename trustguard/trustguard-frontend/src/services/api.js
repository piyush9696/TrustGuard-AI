async function request(url, options = {})
{
    const response = await fetch(url, {
        ...options,
        headers: {
            "Content-Type": "application/json",
            ...(options.headers || {})
        }
    });

    let data = null;

    try
    {
        data = await response.json();
    }
    catch
    {
        data = null;
    }

    if (!response.ok)
    {
        const error = new Error(
            data?.message || "Request failed"
        );

        error.status = response.status;
        error.retryAfter =
            response.headers.get("Retry-After");

        throw error;
    }

    return data;
}

export async function login(email, password)
{
    return request("/api/auth/login", {
        method: "POST",
        body: JSON.stringify({
            email,
            password
        })
    });
}

export async function register(
    username,
    email,
    password
)
{
    return request("/api/auth/register", {
        method: "POST",
        body: JSON.stringify({
            username,
            email,
            password
        })
    });
}

export async function analyze(text)
{
    const token =
        localStorage.getItem("token");

    return request("/api/analyze", {
        method: "POST",
        headers: {
            Authorization: `Bearer ${token}`
        },
        body: JSON.stringify({
            text
        })
    });
}