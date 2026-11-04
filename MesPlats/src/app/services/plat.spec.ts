import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { PlatService } from './plat';

describe('PlatService', () => {
  let service: PlatService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient()]
    });
    service = TestBed.inject(PlatService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
