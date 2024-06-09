import React, { useEffect, useState } from 'react';
import axios from 'axios';
import './ManageAccount.css'; // Create CSS file as needed

const ManageAccount = () => {
    const [users, setUsers] = useState([]);
    const [staffs, setStaffs] = useState([]);
    const [loading, setLoading] = useState(true);
    const [selectedAccount, setSelectedAccount] = useState(null);
    const [accountDetails, setAccountDetails] = useState(null);
    const [error, setError] = useState('');
    const [role, setRole] = useState(1); // Assuming the role of the logged-in user
    const [message, setMessage] = useState(''); // For success/error messages

    useEffect(() => {
        const fetchAccounts = async () => {
            try {
                const userResponse = await axios.get('http://175.41.183.124:4000/api/accounts/users');
                const staffResponse = await axios.get('http://175.41.183.124:4000/api/accounts/staffs');
                setUsers(userResponse.data);
                setStaffs(staffResponse.data);
            } catch (error) {
                console.error('Error fetching accounts:', error);
            } finally {
                setLoading(false);
            }
        };
        fetchAccounts();
    }, []);

    const handleUpdate = async (modifyUserId, modifyRole) => {
        try {
            const response = await axios.put('http://175.41.183.124:4000/api/accounts', null, {
                params: {
                    current_user_id: document.cookie,
                    modify_user_id: modifyUserId,
                    modify_role: modifyRole
                }
            });
            setMessage('Role updated successfully.');
        } catch (error) {
            if (error.response) {
                switch (error.response.status) {
                    case 403:
                        setMessage('You do not have permission to update this role.');
                        break;
                    case 404:
                        setMessage('Cannot find role for modified account.');
                        break;
                    case 304:
                        setMessage('Error when updating role.');
                        break;
                    default:
                        setMessage('An error occurred while updating the role.');
                }
            } else {
                setMessage('An error occurred while updating the role.');
            }
        }
    };

    const handleDelete = async (deleteUserId) => {
        try {
            const response = await axios.delete('http://175.41.183.124:4000/api/accounts', {
                params: {
                    current_user_id: document.cookie,
                    delete_user_id: deleteUserId
                }
            });
            setMessage('Account deleted successfully.');
        } catch (error) {
            if (error.response) {
                switch (error.response.status) {
                    case 403:
                        setMessage('You do not have permission to delete this account.');
                        break;
                    case 404:
                        setMessage('Cannot find delete role account.');
                        break;
                    case 304:
                        setMessage('Error when deleting account.');
                        break;
                    default:
                        setMessage('An error occurred while deleting the account.');
                }
            } else {
                setMessage('An error occurred while deleting the account.');
            }
        }
    };

    const handleAccountClick = async (account) => {
        setSelectedAccount(account);
        try {
            const response = await axios.get('http://175.41.183.124:4000/api/accounts/details', {
                params: { user_id: account.id }
            });
            setAccountDetails(response.data);
            setError('');
        } catch (error) {
            if (error.response && error.response.status === 404) {
                setError('User ID not found or something went wrong.');
            } else {
                setError('An error occurred while fetching account details.');
            }
            setAccountDetails(null);
        }
    };

    const renderAccountDetails = () => {
        if (!selectedAccount) return null;
        return (
            <div className='account-details'>
                <h3>Account Details</h3>
                {error ? (
                    <p className='error'>{error}</p>
                ) : (
                    accountDetails && (
                        <>
                            <p>Name: {accountDetails.name}</p>
                            <p>Username: {selectedAccount.username}</p>
                            {/* Add more details as needed */}
                        </>
                    )
                )}
                <button onClick={() => setSelectedAccount(null)}>Close</button>
            </div>
        );
    };

    const filteredAccounts = (accounts) => {
        return accounts.filter(account => {
            if (role === 1) {
                return account.role === 1 || account.role === 2 || account.role === 3;
            } else if (role === 2) {
                return account.role === 2 || account.role === 3;
            }
            return false;
        });
    };

    return (
        <>
            <div className='manage-account'>
                {loading ? (
                    <p>Loading...</p>
                ) : (
                    <>
                        <h2>Users</h2>
                        <table>
                            <thead>
                                <tr>
                                    <th>Username</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                {filteredAccounts(users).map(user => (
                                    <tr key={user.id}>
                                        <td onClick={() => handleAccountClick(user)}>{user.username}</td>
                                        <td>
                                            <button onClick={() => handleUpdate(user.id, '2')}>Update</button>
                                            <button onClick={() => handleDelete(user.id)}>Delete</button>
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>

                        <h2>Staffs</h2>
                        <table>
                            <thead>
                                <tr>
                                    <th>Username</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                {filteredAccounts(staffs).map(staff => (
                                    <tr key={staff.id}>
                                        <td onClick={() => handleAccountClick(staff)}>{staff.username}</td>
                                        <td>
                                            <button onClick={() => handleUpdate(staff.id, '1')}>Update</button>
                                            <button onClick={() => handleDelete(staff.id)}>Delete</button>
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>

                        {renderAccountDetails()}
                        {message && <p className='message'>{message}</p>}
                    </>
                )}
            </div>
        </>

    );
};

export default ManageAccount;
