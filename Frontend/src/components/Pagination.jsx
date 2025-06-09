import { useContext, useState } from "react";
import AppContext from "../Context/Context";

const Pagination = () => {
    const {totalPages, currentPage, setCurrentPage} = useContext(AppContext);
    const listComponents = [];


    for (let pageNumber = 0; pageNumber <= totalPages-1; pageNumber++) {
    listComponents.push(
    <li className={`page-item ${currentPage === pageNumber ? 'active' : ''}`}>
    <a className="page-link" href="#" onClick={() => setCurrentPage(pageNumber)}>{pageNumber+1}</a>
    </li>);
    }

    return(
        <>
        <nav style={{paddingBottom : 1}}>
            <ul className="pagination justify-content-center pagination-lg">
                <li className={`page-item ${currentPage === 0 ? 'disabled' : ''}`}>
                    <a className="page-link" href="#" onClick={() => setCurrentPage(currentPage => currentPage - 1)}>Previous</a>
                </li>
                {listComponents}
                <li className={`page-item ${currentPage === (totalPages-1) ? 'disabled' : ''}`}>
                    <a className="page-link" href="#" onClick={() => setCurrentPage(currentPage => currentPage + 1)}>Next</a>
                </li>
            </ul>
        </nav>
        </>
    );





}
export default Pagination;