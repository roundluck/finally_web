import { useEffect, useMemo, useState } from 'react';
import { apiClient } from '../api/client.js';
import { useAuth } from '../hooks/useAuth.js';

const PRIORITIES = ['LOW', 'MEDIUM', 'HIGH', 'URGENT'];
const STATUSES = ['NEW', 'UNDER_REVIEW', 'ASSIGNED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED'];

const emptyRequest = {
  title: '',
  description: '',
  dorm: '',
  room: '',
  category: '',
  priority: 'MEDIUM',
  preferredEntryTime: '',
  completionTarget: '',
  assetTag: '',
  photoUrl: '',
};

const Dashboard = () => {
  const { profile, logout, token } = useAuth();
  const [requests, setRequests] = useState([]);
  const [technicians, setTechnicians] = useState([]);
  const [filters, setFilters] = useState({ status: '', dorm: '', mine: false });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [form, setForm] = useState(emptyRequest);
  const [formError, setFormError] = useState(null);
  const [assignTarget, setAssignTarget] = useState(null);
  const [assignForm, setAssignForm] = useState({ technicianId: '', completionTarget: '', note: '' });

  const role = profile?.role;
  const isStudent = role === 'STUDENT';
  const isManager = role === 'MANAGER';
  const isTechnician = role === 'TECHNICIAN';

  useEffect(() => {
    loadRequests();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [filters.status, filters.dorm, filters.mine, token]);

  useEffect(() => {
    if (isManager) {
      apiClient
        .listTechnicians(token)
        .then(setTechnicians)
        .catch((err) => console.error(err.message));
    }
  }, [isManager, token]);

  const activeRequests = useMemo(() => requests.filter((req) => req.status !== 'COMPLETED'), [requests]);

  async function loadRequests() {
    setLoading(true);
    setError(null);
    try {
      const query = {
        status: filters.status,
        dorm: filters.dorm,
        mine: filters.mine,
      };
      const data = await apiClient.listRequests(token, query);
      setRequests(data);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }

  const handleFilterChange = (event) => {
    const { name, value, type, checked } = event.target;
    setFilters((prev) => ({ ...prev, [name]: type === 'checkbox' ? checked : value }));
  };

  const handleRequestChange = (event) => {
    const { name, value } = event.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  };

  const handleCreateRequest = async (event) => {
    event.preventDefault();
    setFormError(null);
    try {
      const payload = {
        ...form,
        preferredEntryTime: form.preferredEntryTime ? new Date(form.preferredEntryTime).toISOString() : null,
        completionTarget: form.completionTarget ? new Date(form.completionTarget).toISOString() : null,
      };
      await apiClient.createRequest(token, payload);
      setForm(emptyRequest);
      await loadRequests();
    } catch (err) {
      setFormError(err.message);
    }
  };

  const updateStatus = async (requestId, status, note) => {
    await apiClient.updateStatus(token, requestId, { status, note });
    await loadRequests();
  };

  const openAssignDialog = (request) => {
    setAssignTarget(request);
    setAssignForm({ technicianId: '', completionTarget: '', note: '' });
  };

  const submitAssignment = async (event) => {
    event.preventDefault();
    if (!assignTarget) return;
    await apiClient.assignTechnician(token, assignTarget.id, {
      technicianId: Number(assignForm.technicianId),
      completionTarget: assignForm.completionTarget ? new Date(assignForm.completionTarget).toISOString() : null,
      note: assignForm.note,
    });
    setAssignTarget(null);
    await loadRequests();
  };

  const renderActions = (request) => {
    if (isStudent) {
      if (['NEW', 'UNDER_REVIEW', 'ASSIGNED'].includes(request.status)) {
        return (
          <button onClick={() => updateStatus(request.id, 'CANCELLED', 'Cancelled by student')} className="ghost">
            Cancel
          </button>
        );
      }
      return null;
    }
    if (isTechnician) {
      if (request.status === 'ASSIGNED') {
        return (
          <button onClick={() => updateStatus(request.id, 'IN_PROGRESS', 'Technician en route')}>
            Start Work
          </button>
        );
      }
      if (request.status === 'IN_PROGRESS') {
        return (
          <button onClick={() => updateStatus(request.id, 'COMPLETED', 'Issue resolved')}>
            Mark Complete
          </button>
        );
      }
      return null;
    }
    if (isManager) {
      return (
        <div className="actions">
          {request.status === 'NEW' && (
            <button onClick={() => updateStatus(request.id, 'UNDER_REVIEW', 'Manager reviewing request')}>
              Move to Review
            </button>
          )}
          {['NEW', 'UNDER_REVIEW'].includes(request.status) && (
            <button className="ghost" onClick={() => updateStatus(request.id, 'CANCELLED', 'Manager cancelled')}>
              Cancel
            </button>
          )}
          {['NEW', 'UNDER_REVIEW', 'ASSIGNED'].includes(request.status) && request.status !== 'COMPLETED' && (
            <button className="secondary" onClick={() => openAssignDialog(request)}>
              Assign Tech
            </button>
          )}
        </div>
      );
    }
    return null;
  };

  return (
    <div className="app-shell">
      <header className="app-header">
        <div>
          <h1>Dormitory Maintenance</h1>
          <p className="muted">{profile?.dorm ? `${profile.dorm} · ${role}` : role}</p>
        </div>
        <div className="header-actions">
          <span>{profile?.fullName}</span>
          <button onClick={logout} className="ghost">
            Logout
          </button>
        </div>
      </header>

      <main className="layout">
        <section className="card">
          <header className="section-header">
            <h2>Filters</h2>
            <p className="muted">Narrow down the request list.</p>
          </header>
          <div className="filters">
            <label>
              Status
              <select name="status" value={filters.status} onChange={handleFilterChange}>
                <option value="">All</option>
                {STATUSES.map((status) => (
                  <option key={status} value={status}>
                    {status}
                  </option>
                ))}
              </select>
            </label>
            <label>
              Dorm/Block
              <input name="dorm" value={filters.dorm} onChange={handleFilterChange} placeholder="e.g. North Hill" />
            </label>
            {isManager && (
              <label className="checkbox">
                <input type="checkbox" name="mine" checked={filters.mine} onChange={handleFilterChange} />
                Show only assignments I manage
              </label>
            )}
          </div>
        </section>

        {isStudent && (
          <section className="card">
            <header className="section-header">
              <h2>Submit New Request</h2>
              <p className="muted">Provide as much detail as possible so managers can prioritise quickly.</p>
            </header>
            <form className="grid" onSubmit={handleCreateRequest}>
              <label>
                Title
                <input name="title" value={form.title} onChange={handleRequestChange} required />
              </label>
              <label>
                Category
                <input name="category" value={form.category} onChange={handleRequestChange} />
              </label>
              <label>
                Priority
                <select name="priority" value={form.priority} onChange={handleRequestChange}>
                  {PRIORITIES.map((priority) => (
                    <option key={priority} value={priority}>
                      {priority}
                    </option>
                  ))}
                </select>
              </label>
              <label>
                Dorm / Block
                <input name="dorm" value={form.dorm} onChange={handleRequestChange} required />
              </label>
              <label>
                Room / Area
                <input name="room" value={form.room} onChange={handleRequestChange} />
              </label>
              <label className="span-2">
                Description
                <textarea name="description" value={form.description} onChange={handleRequestChange} required />
              </label>
              <label>
                Preferred Entry Time
                <input type="datetime-local" name="preferredEntryTime" value={form.preferredEntryTime} onChange={handleRequestChange} />
              </label>
              <label>
                Target Completion
                <input type="datetime-local" name="completionTarget" value={form.completionTarget} onChange={handleRequestChange} />
              </label>
              <label>
                Asset Tag / Reference
                <input name="assetTag" value={form.assetTag} onChange={handleRequestChange} />
              </label>
              <label>
                Photo URL
                <input name="photoUrl" value={form.photoUrl} onChange={handleRequestChange} placeholder="https://" />
              </label>
              {formError && <p className="error span-2">{formError}</p>}
              <div className="span-2 align-right">
                <button type="submit">Create Request</button>
              </div>
            </form>
          </section>
        )}

        <section className="card">
          <header className="section-header">
            <div>
              <h2>Requests ({requests.length})</h2>
              <p className="muted">{activeRequests.length} active · {requests.length - activeRequests.length} closed</p>
            </div>
            <button className="ghost" onClick={loadRequests}>
              Refresh
            </button>
          </header>
          {error && <p className="error">{error}</p>}
          {loading ? (
            <p>Loading requests…</p>
          ) : (
            <div className="table-wrapper">
              <table>
                <thead>
                  <tr>
                    <th>Title</th>
                    <th>Dorm</th>
                    <th>Priority</th>
                    <th>Status</th>
                    <th>Assigned Tech</th>
                    <th>Updated</th>
                    <th>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {requests.map((request) => (
                    <tr key={request.id}>
                      <td>
                        <strong>{request.title}</strong>
                        <p className="muted small">
                          {(request.description ?? '').slice(0, 80)}
                          {(request.description ?? '').length > 80 ? '…' : ''}
                        </p>
                      </td>
                      <td>
                        <span className="label">{request.dorm}</span>
                        {request.room && <p className="muted small">Room {request.room}</p>}
                      </td>
                      <td><span className={`chip priority-${request.priority.toLowerCase()}`}>{request.priority}</span></td>
                      <td><span className={`chip status-${request.status.toLowerCase()}`}>{request.status}</span></td>
                      <td>{request.technician ? request.technician.fullName : '—'}</td>
                      <td>{new Date(request.updatedAt).toLocaleString()}</td>
                      <td>{renderActions(request)}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </section>
      </main>

      {assignTarget && (
        <div className="modal">
          <form className="modal-card" onSubmit={submitAssignment}>
            <h3>Assign Technician</h3>
            <p className="muted">{assignTarget.title}</p>
            <label>
              Technician
              <select
                required
                value={assignForm.technicianId}
                onChange={(event) => setAssignForm((prev) => ({ ...prev, technicianId: event.target.value }))}
              >
                <option value="">Select one</option>
                {technicians.map((tech) => (
                  <option key={tech.id} value={tech.id}>
                    {tech.fullName}
                  </option>
                ))}
              </select>
            </label>
            <label>
              Target completion
              <input
                type="datetime-local"
                value={assignForm.completionTarget}
                onChange={(event) => setAssignForm((prev) => ({ ...prev, completionTarget: event.target.value }))}
              />
            </label>
            <label>
              Note
              <textarea value={assignForm.note} onChange={(event) => setAssignForm((prev) => ({ ...prev, note: event.target.value }))} />
            </label>
            <div className="modal-actions">
              <button type="button" className="ghost" onClick={() => setAssignTarget(null)}>
                Close
              </button>
              <button type="submit">Assign</button>
            </div>
          </form>
        </div>
      )}
    </div>
  );
};

export default Dashboard;
