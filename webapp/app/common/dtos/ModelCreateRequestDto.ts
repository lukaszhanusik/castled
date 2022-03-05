export interface ModelCreateRequestDto {
    warehouseId: number;
    name:  string;
    type:  string;
    details:{
       type: string;
       sourceQuery: string;
    };
    queryPK:{
       primaryKeys: string[];
    };
 }