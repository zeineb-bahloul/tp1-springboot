import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'searchFilter'
})
export class SearchFilterPipe implements PipeTransform {

  transform(list: any[], filterText: string): any {
    if (!list) return [];
    if (!filterText) return list;
    return list.filter(item => {
      const nom = item.intitulePlat || item.nomProduit || '';
      return nom.toLowerCase().includes(filterText.toLowerCase());
    });
  }

}
