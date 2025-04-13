import unittest
import numpy as np
from attractor import lorenz, compute_lorenz_trajectory

class TestLorenzSystem(unittest.TestCase):

    def test_lorenz_single_step(self):
        x, y, z = 1.0, 1.0, 1.0
        s, r, b = 10, 28, 2.667
        x_dot, y_dot, z_dot = lorenz(x, y, z, s, r, b)

        self.assertAlmostEqual(x_dot, 0.0)
        self.assertAlmostEqual(y_dot, 26.0)
        self.assertAlmostEqual(z_dot, -1.667)

    def test_lorenz_custom_parameters(self):
        x_dot, y_dot, z_dot = lorenz(2.0, 3.0, 4.0, s=15, r=30, b=2.5)
        self.assertAlmostEqual(x_dot, 15.0)
        self.assertAlmostEqual(y_dot, 30*2 - 3 - 2*4, delta=1e-6)
        self.assertAlmostEqual(z_dot, 2*3 - 2.5*4, delta=1e-6)

    def test_trajectory_initial_values(self):
        x0, y0, z0 = 1.0, 1.0, 1.0
        xs, ys, zs = compute_lorenz_trajectory(x0, y0, z0, num_steps=10)
        self.assertEqual(xs[0], x0)
        self.assertEqual(ys[0], y0)
        self.assertEqual(zs[0], z0)

    def test_trajectory_length(self):
        num_steps = 500
        xs, ys, zs = compute_lorenz_trajectory(0.0, 1.0, 1.05, num_steps=num_steps)
        self.assertEqual(len(xs), num_steps + 1)
        self.assertEqual(len(ys), num_steps + 1)
        self.assertEqual(len(zs), num_steps + 1)

    def test_trajectory_type(self):
        xs, ys, zs = compute_lorenz_trajectory(0.0, 1.0, 1.05)
        self.assertIsInstance(xs, np.ndarray)
        self.assertIsInstance(ys, np.ndarray)
        self.assertIsInstance(zs, np.ndarray)

if __name__ == '__main__':
    unittest.main()
