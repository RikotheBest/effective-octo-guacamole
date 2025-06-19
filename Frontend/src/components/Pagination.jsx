import { useContext, useEffect, useState } from "react";
import AppContext from "../Context/Context";

const Pagination = () => {
    const {totalPages, currentPage, setCurrentPage} = useContext(AppContext);
    let listComponents = [];
    const DOTS = "..."
    const siblingCount = 1;
    const totalPaginationElements = siblingCount + 5;
    const leftSiblingIndex = Math.max((currentPage+1) - siblingCount, 1);
    const rightSiblingIndex = Math.min((currentPage+1) + siblingCount, totalPages);
    const shouldShowLeftDots = leftSiblingIndex > 2;
    const shouldShowRightDots = rightSiblingIndex < totalPages - 2;
    const firstPageIndex = 1;
    const lastPageIndex = totalPages;


    
    const range = (start, end) => {
        const length = end - start + 1;
        listComponents = Array.from({length}, (_, i) => i + start)
    }


    
    if(totalPages <= totalPaginationElements){
        range(1, totalPages);
    } else if (!shouldShowLeftDots && shouldShowRightDots) {
      let leftItemCount = 3 + 2 * siblingCount;
      range(1, leftItemCount);
      listComponents = [...listComponents, DOTS, totalPages];

    } else if (shouldShowLeftDots && !shouldShowRightDots) {
      let rightItemCount = 3 + 2 * siblingCount;
      range(totalPages - rightItemCount + 1, totalPages);
      listComponents = [firstPageIndex, DOTS, ...listComponents];

    } else if (shouldShowLeftDots && shouldShowRightDots) {
      range(leftSiblingIndex, rightSiblingIndex);
      listComponents = [firstPageIndex, DOTS, ...listComponents, DOTS, lastPageIndex];
    }
   
  

    if (listComponents.length < 2) {
        console.log("returning null")
        return null;
    }

    return(
        <>
        <nav style={{paddingBottom : 1}}>
            <ul className="pagination justify-content-center pagination-lg">
                <li className={`page-item ${currentPage === 0 ? 'disabled' : ''}`}>
                    <a className="page-link" href="#" onClick={() => setCurrentPage(currentPage => currentPage - 1)}>Previous</a>
                </li>
             
                {listComponents.map(pageNumber => {

                    // If the pageItem is a DOT, render the DOTS unicode character
                    if (pageNumber === DOTS) {
                    return  <li className="page-item">
                        <a className="page-link" href="#">&#8230;</a>
                            </li>;
                    }

                    // Render our Page Pills
                    return (
                    <li className= {`page-item ${currentPage === (pageNumber - 1) ? 'active' : ''}`}>
                        <a className="page-link" href="#" onClick={() => setCurrentPage(pageNumber - 1)}> {pageNumber} </a>
                    </li>
                    );
                })}

                <li className={`page-item ${currentPage === (totalPages-1) ? 'disabled' : ''}`}>
                    <a className="page-link" href="#" onClick={() => setCurrentPage(currentPage => currentPage + 1)}>Next</a>
                </li>
            </ul>
        </nav>
        </>
    );





}
export default Pagination;