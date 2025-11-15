import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import { describe, it, expect, vi } from 'vitest';
import LoginPage from './LoginPage.jsx';
import { AuthContext } from '../contexts/AuthContext.js';

const renderWithProviders = (ui, contextValue = {}) => {
  return render(
    <AuthContext.Provider value={contextValue}>
      <MemoryRouter>{ui}</MemoryRouter>
    </AuthContext.Provider>
  );
};

describe('LoginPage', () => {
  it('submits credentials and calls login', async () => {
    const login = vi.fn().mockResolvedValue({});
    renderWithProviders(<LoginPage />, { login });

    fireEvent.change(screen.getByLabelText(/username/i), { target: { value: 'alicelee' } });
    fireEvent.change(screen.getByLabelText(/password/i), { target: { value: 'Password!23' } });
    fireEvent.click(screen.getByRole('button', { name: /login/i }));

    await waitFor(() => expect(login).toHaveBeenCalledWith('alicelee', 'Password!23'));
  });

  it('shows error message when login fails', async () => {
    const login = vi.fn().mockRejectedValue(new Error('bad credentials'));
    renderWithProviders(<LoginPage />, { login });

    fireEvent.change(screen.getByLabelText(/username/i), { target: { value: 'invalid' } });
    fireEvent.change(screen.getByLabelText(/password/i), { target: { value: 'nope' } });
    fireEvent.click(screen.getByRole('button', { name: /login/i }));

    const error = await screen.findByText(/bad credentials/i);
    expect(error).toBeInTheDocument();
  });
});
