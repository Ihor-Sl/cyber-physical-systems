import numpy as np
import matplotlib.pyplot as plt
from matplotlib.animation import FuncAnimation

def lorenz(x, y, z, s=10, r=28, b=2.667):
    x_dot = s * (y - x)
    y_dot = x * (r - z) - y
    z_dot = x * y - b * z
    return x_dot, y_dot, z_dot

def compute_lorenz_trajectory(x0, y0, z0, dt=0.01, num_steps=10000):
    xs = np.empty(num_steps + 1)
    ys = np.empty(num_steps + 1)
    zs = np.empty(num_steps + 1)
    xs[0], ys[0], zs[0] = x0, y0, z0

    for i in range(num_steps):
        x_dot, y_dot, z_dot = lorenz(xs[i], ys[i], zs[i])
        xs[i + 1] = xs[i] + x_dot * dt
        ys[i + 1] = ys[i] + y_dot * dt
        zs[i + 1] = zs[i] + z_dot * dt

    return xs, ys, zs

if __name__ == '__main__':
    dt = 0.01
    num_steps = 10000

    xs1, ys1, zs1 = compute_lorenz_trajectory(1.0, 1.0, 1.0, dt, num_steps)
    xs2, ys2, zs2 = compute_lorenz_trajectory(1.0001, 1.0, 1.0, dt, num_steps)

    diff = np.sqrt((xs1 - xs2) ** 2 + (ys1 - ys2) ** 2 + (zs1 - zs2) ** 2)


    fig = plt.figure(figsize=(14, 6))

    ax1 = fig.add_subplot(1, 2, 1, projection='3d')
    line1, = ax1.plot([], [], [], lw=0.5, label="Графік 1", alpha=0.7)
    line2, = ax1.plot([], [], [], lw=0.5, label="Графік 2", alpha=0.7)
    ax1.set_title("Траекторії аттракторів Лоренца")
    ax1.set_xlabel("X")
    ax1.set_ylabel("Y")
    ax1.set_zlabel("Z")
    ax1.legend()

    ax1.set_xlim([min(xs1.min(), xs2.min()), max(xs1.max(), xs2.max())])
    ax1.set_ylim([min(ys1.min(), ys2.min()), max(ys1.max(), ys2.max())])
    ax1.set_zlim([min(zs1.min(), zs2.min()), max(zs1.max(), zs2.max())])

    ax2 = fig.add_subplot(1, 2, 2)
    line2d, = ax2.plot([], [], label="Різниця")
    ax2.set_title("Різниця між траекторіями")
    ax2.set_xlabel("Час")
    ax2.set_ylabel("Різниця")

    ax2.set_xlim([0, num_steps * dt])
    ax2.set_ylim([0, np.max(diff)])

    def update(frame):
        line1.set_data(xs1[:frame], ys1[:frame])
        line1.set_3d_properties(zs1[:frame])

        line2.set_data(xs2[:frame], ys2[:frame])
        line2.set_3d_properties(zs2[:frame])

        line2d.set_data(np.arange(frame) * dt, diff[:frame])

        return line1, line2, line2d

    ani = FuncAnimation(fig, update, frames=range(1, num_steps), interval=1, blit=True)

    plt.tight_layout()
    plt.show()